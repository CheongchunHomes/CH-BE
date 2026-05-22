package com.chcorp.homes.subscription.service;

import com.chcorp.homes.subscription.dto.ApplyhomeAptCompetitionApiResponse;
import com.chcorp.homes.subscription.dto.SubscriptionCompetitionImportResultDTO;
import com.chcorp.homes.subscription.entity.SubscriptionCompetitionRate;
import com.chcorp.homes.subscription.entity.SubscriptionHouseType;
import com.chcorp.homes.subscription.repository.SubscriptionCompetitionRateRepository;
import com.chcorp.homes.subscription.repository.SubscriptionHouseTypeRepository;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionCompetitionImportService {

    private final SubscriptionHouseTypeRepository subscriptionHouseTypeRepository;
    private final SubscriptionCompetitionRateRepository subscriptionCompetitionRateRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${api.subscription.applyhome.competition-service-key:}")
    private String applyhomeCompetitionServiceKey;

    private static final String APT_COMPETITION_URL =
            "https://api.odcloud.kr/api/15101048/v1/uddi:2f83a0c5-ef17-4c1a-bee6-d53e37fd67e5";

    @Transactional
    public SubscriptionCompetitionImportResultDTO importApplyhomeAptCompetitionRates() {
        if (applyhomeCompetitionServiceKey == null || applyhomeCompetitionServiceKey.isBlank()) {
            throw new IllegalStateException("API_SUBSCRIPTION_APPLYHOME_COMPETITION_SERVICE_KEY 환경변수가 설정되지 않았습니다.");
        }

        // 기존 경쟁률 데이터를 삭제하고 최신 데이터로 다시 저장한다.
        subscriptionCompetitionRateRepository.deleteAllInBatch();

        // 주택형 데이터를 기준으로 공공데이터 응답과 우리 공고를 매칭한다.
        Map<String, Long> announcementIdMap = buildAnnouncementIdMap();

        int page = 1;
        int perPage = 1000;
        int totalApiCount = 0;
        int savedCount = 0;
        int skippedCount = 0;
        int failedCount = 0;

        while (true) {
            try {
                String url = UriComponentsBuilder
                        .fromUriString(APT_COMPETITION_URL)
                        .queryParam("page", page)
                        .queryParam("perPage", perPage)
                        .queryParam("returnType", "JSON")
                        .queryParam("serviceKey", applyhomeCompetitionServiceKey)
                        .build(false)
                        .toUriString();

                // APT 경쟁률 API를 페이지 단위로 호출한다.
                ResponseEntity<ApplyhomeAptCompetitionApiResponse> response =
                        restTemplate.getForEntity(url, ApplyhomeAptCompetitionApiResponse.class);

                ApplyhomeAptCompetitionApiResponse body = response.getBody();

                if (body == null || body.getData() == null || body.getData().isEmpty()) {
                    break;
                }

                totalApiCount = body.getTotalCount();

                for (ApplyhomeAptCompetitionApiResponse.Item item : body.getData()) {
                    Long announcementId = findAnnouncementId(announcementIdMap, item);

                    if (announcementId == null) {
                        skippedCount++;
                        continue;
                    }

                    SubscriptionCompetitionRate rate = SubscriptionCompetitionRate.builder()
                            .announcementId(announcementId)
                            .houseManageNo(clean(item.getHouseManageNo()))
                            .pblancNo(clean(item.getPblancNo()))
                            .modelNo(clean(item.getModelNo()))
                            .houseTypeName(clean(item.getHouseTypeName()))
                            .supplyCount(item.getSupplyCount())
                            .rankNo(item.getRankNo())
                            .residenceCode(item.getResidenceCode())
                            .residenceArea(clean(item.getResidenceArea()))
                            .requestCount(item.getRequestCount())
                            .competitionRate(clean(item.getCompetitionRate()))
                            .rawData(toJson(item))
                            .build();

                    subscriptionCompetitionRateRepository.save(rate);
                    savedCount++;
                }

                log.info("[APT 경쟁률] page={}, currentCount={}, savedCount={}",
                        page,
                        body.getCurrentCount(),
                        savedCount
                );

                if (page * perPage >= body.getTotalCount()) {
                    break;
                }

                page++;
            } catch (Exception e) {
                failedCount++;
                log.error("[APT 경쟁률] import 실패 page={}", page, e);
                break;
            }
        }

        return SubscriptionCompetitionImportResultDTO.builder()
                .totalApiCount(totalApiCount)
                .savedCount(savedCount)
                .skippedCount(skippedCount)
                .failedCount(failedCount)
                .build();
    }

    private Map<String, Long> buildAnnouncementIdMap() {
        // houseManageNo, pblancNo, modelNo 기준 매칭용 Map을 만든다.
        List<SubscriptionHouseType> houseTypes =
                subscriptionHouseTypeRepository.findByHouseManageNoIsNotNullAndPblancNoIsNotNull();

        Map<String, Long> map = new HashMap<>();

        for (SubscriptionHouseType houseType : houseTypes) {
            String houseManageNo = clean(houseType.getHouseManageNo());
            String pblancNo = clean(houseType.getPblancNo());
            String modelNo = clean(houseType.getModelNo());

            if (houseManageNo == null || pblancNo == null) {
                continue;
            }

            // 모델번호까지 일치하는 정확한 매칭 키를 저장한다.
            if (modelNo != null) {
                map.put(houseManageNo + "|" + pblancNo + "|" + modelNo, houseType.getAnnouncementId());
            }

            // 모델번호가 없을 때를 대비해 공고 단위 매칭 키도 저장한다.
            map.putIfAbsent(houseManageNo + "|" + pblancNo, houseType.getAnnouncementId());
        }

        return map;
    }

    private Long findAnnouncementId(
            Map<String, Long> announcementIdMap,
            ApplyhomeAptCompetitionApiResponse.Item item
    ) {
        // 모델번호까지 맞는 경우를 먼저 찾는다.
        String houseManageNo = clean(item.getHouseManageNo());
        String pblancNo = clean(item.getPblancNo());
        String modelNo = clean(item.getModelNo());

        if (houseManageNo == null || pblancNo == null) {
            return null;
        }

        if (modelNo != null) {
            Long exactMatch = announcementIdMap.get(houseManageNo + "|" + pblancNo + "|" + modelNo);

            if (exactMatch != null) {
                return exactMatch;
            }
        }

        return announcementIdMap.get(houseManageNo + "|" + pblancNo);
    }

    private String clean(String value) {
        // 빈 문자열은 null로 통일한다.
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private String toJson(Object value) {
        // 원본 응답을 JSON 문자열로 저장한다.
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}