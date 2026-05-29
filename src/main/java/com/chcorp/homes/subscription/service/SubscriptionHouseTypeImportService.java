package com.chcorp.homes.subscription.service;

import com.chcorp.homes.subscription.dto.ApplyhomeHouseTypeApiResponse;
import com.chcorp.homes.subscription.dto.SubscriptionHouseTypeImportResultDTO;
import com.chcorp.homes.subscription.entity.SubscriptionHouseType;
import com.chcorp.homes.subscription.repository.SubscriptionHouseTypeRepository;
import com.chcorp.homes.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionHouseTypeImportService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionHouseTypeRepository subscriptionHouseTypeRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${api.subscription.applyhome.service-key:}")
    private String applyhomeServiceKey;

    private static final String APT_HOUSE_TYPE_URL =
            "https://api.odcloud.kr/api/ApplyhomeInfoDetailSvc/v1/getAPTLttotPblancMdl";

    private static final int PER_PAGE = 100;

    @Transactional
    public SubscriptionHouseTypeImportResultDTO importApplyhomeAptHouseTypes() {
        if (applyhomeServiceKey == null || applyhomeServiceKey.isBlank()) {
            throw new IllegalStateException("API_SUBSCRIPTION_APPLYHOME_SERVICE_KEY 환경변수가 설정되지 않았습니다.");
        }

        log.info("[청약홈 APT 주택형] 공공데이터 수집 시작. serviceKey length={}", applyhomeServiceKey.length());

        int page = 1;
        int totalApiCount = 0;
        int matchedAnnouncementCount = 0;
        int savedCount = 0;
        int skippedCount = 0;

        while (true) {
            String url = UriComponentsBuilder
                    .fromUriString(APT_HOUSE_TYPE_URL)
                    .queryParam("page", page)
                    .queryParam("perPage", PER_PAGE)
                    .queryParam("serviceKey", applyhomeServiceKey)
                    .build(false)
                    .toUriString();

            log.info("[청약홈 APT 주택형] API 호출 page={}", page);

            ResponseEntity<ApplyhomeHouseTypeApiResponse> response =
                    restTemplate.getForEntity(url, ApplyhomeHouseTypeApiResponse.class);

            ApplyhomeHouseTypeApiResponse body = response.getBody();

            if (body == null || body.getData() == null || body.getData().isEmpty()) {
                log.info("[청약홈 APT 주택형] {}페이지 데이터 없음. 수집 종료", page);
                break;
            }

            if (page == 1) {
                totalApiCount = body.getTotalCount();
                log.info("[청약홈 APT 주택형] 총 데이터 수: {}", totalApiCount);
            }

            for (ApplyhomeHouseTypeApiResponse.Item item : body.getData()) {
                String pblancNo = item.getPblancNo();

                if (isBlank(pblancNo)) {
                    skippedCount++;
                    continue;
                }

                Long announcementId = subscriptionRepository
                        .findAnnouncementIdByPblancNo(pblancNo)
                        .orElse(null);

                if (announcementId == null) {
                    skippedCount++;
                    continue;
                }

                matchedAnnouncementCount++;

                boolean exists = subscriptionHouseTypeRepository
                        .existsByPblancNoAndModelNoAndHouseTypeName(
                                pblancNo,
                                item.getModelNo(),
                                item.getHouseTypeName()
                        );

                if (exists) {
                    skippedCount++;
                    continue;
                }

                SubscriptionHouseType houseType = SubscriptionHouseType.builder()
                        .announcementId(announcementId)
                        .houseManageNo(item.getHouseManageNo())
                        .pblancNo(pblancNo)
                        .modelNo(item.getModelNo())
                        .houseTypeName(item.getHouseTypeName())
                        .exclusiveArea(item.getExclusiveArea())
                        .supplyHouseholdCount(toInteger(item.getSupplyHouseholdCount()))
                        .specialSupplyCount(toInteger(item.getSpecialSupplyCount()))
                        .generalSupplyCount(toInteger(item.getGeneralSupplyCount()))
                        .supplyPrice(toLong(item.getSupplyPrice()))
                        .rentDeposit(null)
                        .monthlyRent(null)
                        .build();

                subscriptionHouseTypeRepository.save(houseType);
                savedCount++;
            }

            log.info("[청약홈 APT 주택형] {}페이지 처리 완료, 현재 저장 {}건", page, savedCount);

            if (totalApiCount == 0 || page * PER_PAGE >= totalApiCount) {
                break;
            }

            page++;
        }

        log.info(
                "[청약홈 APT 주택형] 수집 완료 totalApiCount={}, matched={}, saved={}, skipped={}",
                totalApiCount,
                matchedAnnouncementCount,
                savedCount,
                skippedCount
        );

        return SubscriptionHouseTypeImportResultDTO.builder()
                .totalApiCount(totalApiCount)
                .matchedAnnouncementCount(matchedAnnouncementCount)
                .savedCount(savedCount)
                .skippedCount(skippedCount)
                .build();
    }

    private Integer toInteger(String value) {
        Long parsed = toLong(value);

        if (parsed == null) {
            return null;
        }

        if (parsed > Integer.MAX_VALUE) {
            return null;
        }

        return parsed.intValue();
    }

    private Long toLong(String value) {
        if (isBlank(value)) {
            return null;
        }

        try {
            String cleaned = value
                    .replace(",", "")
                    .replace("원", "")
                    .replace("만원", "")
                    .trim();

            if (cleaned.contains(".")) {
                cleaned = cleaned.substring(0, cleaned.indexOf("."));
            }

            if (cleaned.isBlank()) {
                return null;
            }

            return Long.parseLong(cleaned);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}