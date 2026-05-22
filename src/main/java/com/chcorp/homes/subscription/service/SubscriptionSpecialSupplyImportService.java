package com.chcorp.homes.subscription.service;

import com.chcorp.homes.subscription.dto.ApplyhomeAptSpecialSupplyApiResponse;
import com.chcorp.homes.subscription.dto.SubscriptionSpecialSupplyImportResultDTO;
import com.chcorp.homes.subscription.entity.SubscriptionHouseType;
import com.chcorp.homes.subscription.entity.SubscriptionSpecialSupplyStat;
import com.chcorp.homes.subscription.repository.SubscriptionHouseTypeRepository;
import com.chcorp.homes.subscription.repository.SubscriptionSpecialSupplyStatRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionSpecialSupplyImportService {

    private final SubscriptionHouseTypeRepository subscriptionHouseTypeRepository;
    private final SubscriptionSpecialSupplyStatRepository subscriptionSpecialSupplyStatRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${api.subscription.applyhome.service-key:}")
    private String applyhomeServiceKey;

    private static final String APT_SPECIAL_SUPPLY_URL =
            "https://api.odcloud.kr/api/ApplyhomeInfoCmpetRtSvc/v1/getAPTSpsplyReqstStus";

    @Transactional
    public SubscriptionSpecialSupplyImportResultDTO importApplyhomeAptSpecialSupplyStats() {
        if (applyhomeServiceKey == null || applyhomeServiceKey.isBlank()) {
            throw new IllegalStateException("API_SUBSCRIPTION_APPLYHOME_SERVICE_KEY 환경변수가 설정되지 않았습니다.");
        }

        // 주택형 테이블에서 API 호출에 필요한 공고번호와 주택관리번호를 가져온다.
        List<SubscriptionHouseType> houseTypes =
                subscriptionHouseTypeRepository.findByHouseManageNoIsNotNullAndPblancNoIsNotNull();

        Set<String> requestedKeys = new HashSet<>();

        int targetAnnouncementCount = 0;
        int totalApiCount = 0;
        int savedCount = 0;
        int skippedCount = 0;
        int failedCount = 0;

        for (SubscriptionHouseType houseType : houseTypes) {
            String houseManageNo = houseType.getHouseManageNo();
            String pblancNo = houseType.getPblancNo();

            if (isBlank(houseManageNo) || isBlank(pblancNo)) {
                skippedCount++;
                continue;
            }

            String key = houseManageNo + "|" + pblancNo;

            if (!requestedKeys.add(key)) {
                skippedCount++;
                continue;
            }

            targetAnnouncementCount++;

            try {
                String url = UriComponentsBuilder
                        .fromUriString(APT_SPECIAL_SUPPLY_URL)
                        .queryParam("page", 1)
                        .queryParam("perPage", 100)
                        .queryParam("returnType", "JSON")
                        .queryParam("serviceKey", applyhomeServiceKey)
                        .queryParam("cond[HOUSE_MANAGE_NO::EQ]", houseManageNo)
                        .queryParam("cond[PBLANC_NO::EQ]", pblancNo)
                        .build(false)
                        .toUriString();

                // 공고번호와 주택관리번호 기준으로 특별공급 신청현황 API를 호출한다.
                ResponseEntity<ApplyhomeAptSpecialSupplyApiResponse> response =
                        restTemplate.getForEntity(url, ApplyhomeAptSpecialSupplyApiResponse.class);

                ApplyhomeAptSpecialSupplyApiResponse body = response.getBody();

                if (body == null || body.getData() == null || body.getData().isEmpty()) {
                    skippedCount++;
                    continue;
                }

                totalApiCount += body.getTotalCount();

                // 같은 공고의 기존 특별공급 신청현황을 삭제 후 다시 저장한다.
                subscriptionSpecialSupplyStatRepository.deleteByPblancNoAndHouseManageNo(pblancNo, houseManageNo);

                for (Map<String, Object> row : body.getData()) {
                    SubscriptionSpecialSupplyStat stat = SubscriptionSpecialSupplyStat.builder()
                            .announcementId(houseType.getAnnouncementId())
                            .houseManageNo(houseManageNo)
                            .pblancNo(pblancNo)
                            .houseTypeName(getString(row, "HOUSE_TY", "HOUSE_TYPE", "HOUSETY"))
                            .modelNo(getString(row, "MODEL_NO"))
                            .rawData(toJson(row))
                            .build();

                    subscriptionSpecialSupplyStatRepository.save(stat);
                    savedCount++;
                }

                log.info(
                        "[APT 특별공급 신청현황] 저장 완료 houseManageNo={}, pblancNo={}, rows={}",
                        houseManageNo,
                        pblancNo,
                        body.getData().size()
                );
            } catch (Exception e) {
                failedCount++;
                log.warn(
                        "[APT 특별공급 신청현황] 호출 실패 houseManageNo={}, pblancNo={}, message={}",
                        houseManageNo,
                        pblancNo,
                        e.getMessage()
                );
            }
        }

        return SubscriptionSpecialSupplyImportResultDTO.builder()
                .targetAnnouncementCount(targetAnnouncementCount)
                .totalApiCount(totalApiCount)
                .savedCount(savedCount)
                .skippedCount(skippedCount)
                .failedCount(failedCount)
                .build();
    }

    private String getString(Map<String, Object> row, String... keys) {
        // 여러 후보 필드명 중 값이 있는 첫 번째 값을 사용한다.
        for (String key : keys) {
            Object value = row.get(key);

            if (value != null && !value.toString().isBlank()) {
                return value.toString();
            }
        }

        return null;
    }

    private String toJson(Map<String, Object> row) {
        // 원본 응답 row를 JSON 문자열로 저장한다.
        try {
            return objectMapper.writeValueAsString(row);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}