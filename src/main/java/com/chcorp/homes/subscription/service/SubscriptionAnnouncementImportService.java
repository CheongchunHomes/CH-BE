package com.chcorp.homes.subscription.service;

import com.chcorp.homes.subscription.dto.ApplyhomeAptAnnouncementApiResponse;
import com.chcorp.homes.subscription.dto.SubscriptionAnnouncementImportResultDTO;
import com.chcorp.homes.announcements.entity.Announcement;
import com.chcorp.homes.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionAnnouncementImportService {

    private final SubscriptionRepository subscriptionRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${api.subscription.applyhome.service-key:}")
    private String applyhomeServiceKey;

    private static final String APT_ANNOUNCEMENT_URL =
            "https://api.odcloud.kr/api/ApplyhomeInfoDetailSvc/v1/getAPTLttotPblancDetail";

    private static final int PER_PAGE = 100;

    @Transactional
    public SubscriptionAnnouncementImportResultDTO importApplyhomeAptAnnouncements() {
        if (applyhomeServiceKey == null || applyhomeServiceKey.isBlank()) {
            throw new IllegalStateException("API_SUBSCRIPTION_APPLYHOME_SERVICE_KEY 환경변수가 설정되지 않았습니다.");
        }

        log.info("[청약홈 APT 공고] 수집 시작. serviceKey length={}", applyhomeServiceKey.length());

        int page = 1;
        int totalApiCount = 0;
        int savedCount = 0;
        int skippedCount = 0;
        int duplicateCount = 0;

        while (true) {
            String url = UriComponentsBuilder
                    .fromUriString(APT_ANNOUNCEMENT_URL)
                    .queryParam("page", page)
                    .queryParam("perPage", PER_PAGE)
                    .queryParam("returnType", "JSON")
                    .queryParam("serviceKey", applyhomeServiceKey)
                    .queryParam("cond[HOUSE_SECD::EQ]", "01")
                    .queryParam("cond[RCRIT_PBLANC_DE::GTE]", "2024-01-01")
                    .queryParam("cond[RCRIT_PBLANC_DE::LTE]", "2026-12-31")
                    .build(false)
                    .toUriString();

            log.info("[청약홈 APT 공고] API 호출 page={}", page);


            ResponseEntity<String> rawResponse =
                    restTemplate.getForEntity(url, String.class);

            String rawBody = rawResponse.getBody();

            log.info(
                    "[청약홈 APT 공고] RAW 응답 일부={}",
                    rawBody != null && rawBody.length() > 500 ? rawBody.substring(0, 500) : rawBody
            );

            ApplyhomeAptAnnouncementApiResponse body = null;

            try {
                body = new com.fasterxml.jackson.databind.ObjectMapper()
                        .readValue(rawBody, ApplyhomeAptAnnouncementApiResponse.class);
            } catch (Exception e) {
                log.error("[청약홈 APT 공고] 응답 파싱 실패", e);
            }

            if (body == null || body.getData() == null || body.getData().isEmpty()) {
                log.info("[청약홈 APT 공고] {}페이지 데이터 없음. 수집 종료", page);
                break;
            }

            if (page == 1) {
                totalApiCount = body.getTotalCount();
                log.info("[청약홈 APT 공고] 총 데이터 수: {}", totalApiCount);
            }

            for (ApplyhomeAptAnnouncementApiResponse.Item item : body.getData()) {
                String pblancNo = item.getPblancNo();

                if (isBlank(pblancNo)) {
                    skippedCount++;
                    continue;
                }

                if (subscriptionRepository.existsByPblancNo(pblancNo)) {
                    duplicateCount++;
                    continue;
                }

                Announcement announcement = Announcement.builder()
                        .externalId("APPLYHOME-" + pblancNo)
                        .pblancNo(pblancNo)
                        .sourceType("청약홈")
                        .title(defaultText(item.getHouseName(), "청약홈 APT 공고"))
                        .region(item.getRegion())
                        .recuitmentType("아파트")
                        .targetType(buildTargetType(item))
                        .requiresSubAccount(true)
                        .incomeCondition(null)
                        .specialSupplyYn(null)
                        .content(null)
                        .applyStartDate(toLocalDate(item.getApplyStartDate()))
                        .applyEndDate(toLocalDate(item.getApplyEndDate()))
                        .status(resolveStatus(item.getApplyStartDate(), item.getApplyEndDate()))
                        .isVisible(true)
                        .sourceUrl(defaultText(item.getHomepageUrl(), "https://www.applyhome.co.kr"))
                        .address(defaultText(item.getAddress(), "-"))
                        .latitude(null)
                        .longitude(null)
                        .supplyInstitution(item.getSupplyInstitution())
                        .totHshldCo(item.getTotalHouseholdCount())
                        .rentGtn(null)
                        .mtRntchrg(null)
                        .heatMthdNm(null)
                        .beginDe(toLocalDate(item.getNoticeDate()))
                        .endDe(toLocalDate(item.getApplyEndDate()))
                        .cntrctCnclsBgnde(toLocalDate(item.getContractStartDate()))
                        .cntrctCnclsEndde(toLocalDate(item.getContractEndDate()))
                        .mvnPrearngeYm(item.getMoveInExpectedYm())
                        .przwnerPresnatnDe(toLocalDate(item.getWinnerDate()))
                        .build();

                subscriptionRepository.save(announcement);
                savedCount++;
            }

            log.info("[청약홈 APT 공고] {}페이지 처리 완료, 현재 저장 {}건", page, savedCount);

            if (totalApiCount == 0 || page * PER_PAGE >= totalApiCount) {
                break;
            }

            page++;
        }

        log.info(
                "[청약홈 APT 공고] 수집 완료 totalApiCount={}, saved={}, skipped={}, duplicate={}",
                totalApiCount,
                savedCount,
                skippedCount,
                duplicateCount
        );

        return SubscriptionAnnouncementImportResultDTO.builder()
                .totalApiCount(totalApiCount)
                .savedCount(savedCount)
                .skippedCount(skippedCount)
                .duplicateCount(duplicateCount)
                .build();
    }

    private String buildTargetType(ApplyhomeAptAnnouncementApiResponse.Item item) {
        String houseSecdName = item.getHouseSecdName();
        String houseDtlSecdName = item.getHouseDtlSecdName();
        String rentSecdName = item.getRentSecdName();

        StringBuilder builder = new StringBuilder();

        if (!isBlank(houseSecdName)) {
            builder.append(houseSecdName);
        }

        if (!isBlank(houseDtlSecdName)) {
            if (!builder.isEmpty()) {
                builder.append(" / ");
            }
            builder.append(houseDtlSecdName);
        }

        if (!isBlank(rentSecdName)) {
            if (!builder.isEmpty()) {
                builder.append(" / ");
            }
            builder.append(rentSecdName);
        }

        return builder.isEmpty() ? "APT" : builder.toString();
    }

    private String resolveStatus(String startDateText, String endDateText) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = toLocalDate(startDateText);
        LocalDate endDate = toLocalDate(endDateText);

        if (startDate == null || endDate == null) {
            return "상태미정";
        }

        if (today.isBefore(startDate)) {
            return "접수예정";
        }

        if (today.isAfter(endDate)) {
            return "접수마감";
        }

        return "접수중";
    }

    private LocalDate toLocalDate(String value) {
        if (isBlank(value)) {
            return null;
        }

        String cleaned = value.trim();

        try {
            if (cleaned.matches("\\d{8}")) {
                return LocalDate.parse(cleaned, DateTimeFormatter.ofPattern("yyyyMMdd"));
            }

            if (cleaned.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return LocalDate.parse(cleaned);
            }

            if (cleaned.contains("~")) {
                String firstDate = cleaned.split("~")[0].trim();
                return toLocalDate(firstDate);
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private String defaultText(String value, String defaultValue) {
        if (isBlank(value)) {
            return defaultValue;
        }

        return value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}