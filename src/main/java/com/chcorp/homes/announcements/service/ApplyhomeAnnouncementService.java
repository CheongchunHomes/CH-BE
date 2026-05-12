package com.chcorp.homes.announcements.service;

import com.chcorp.homes.announcements.dto.ApplyhomeApiResponse;
import com.chcorp.homes.announcements.entity.Announcement;
import com.chcorp.homes.announcements.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
// 청약홈 api 추가
public class ApplyhomeAnnouncementService {

    private final RestTemplate restTemplate;
    private final AnnouncementRepository repository;

    @Value("${api.applyhome.service-key}")
    private String applyhomeServiceKey;

    private static final String APPLYHOME_BASE_URL =
            "https://api.odcloud.kr/api/ApplyhomeInfoDetailSvc/v1/getUrbtyOfctlLttotPblancDetail";
    private static final int PER_PAGE = 100;

    @Transactional
    public void fetchApplyhome() {
        int page = 1;
        int totalCount = 0;

        do {
            try {
                String url = APPLYHOME_BASE_URL
                        + "?page=" + page
                        + "&perPage=" + PER_PAGE
                        + "&serviceKey=" + applyhomeServiceKey;

                ResponseEntity<ApplyhomeApiResponse> response =
                        restTemplate.getForEntity(url, ApplyhomeApiResponse.class);

                ApplyhomeApiResponse apiResponse = response.getBody();

                if (apiResponse == null || apiResponse.getData() == null) {
                    log.warn("청약홈 API {}페이지 응답 body 없음", page);
                    break;
                }

                if (page == 1) {
                    totalCount = apiResponse.getTotalCount();
                    log.info("청약홈 API 총 데이터 수: {}", totalCount);
                }

                List<ApplyhomeApiResponse.Item> items = apiResponse.getData();
                log.info("청약홈 API {}페이지 데이터 수: {}", page, items.size());

                if (items.isEmpty()) {
                    log.info("[청약홈] {}페이지 수집 종료 - 데이터 없음", page);
                    break;
                }

                for (ApplyhomeApiResponse.Item item : items) {
                    String externalId = item.getPblancNo();

                    if (externalId == null || externalId.isBlank()) {
                        log.warn("[청약홈] {}페이지 PBLANC_NO 없음 - 저장 건너뜀", page);
                        continue;
                    }

//                    if (repository.existsByExternalId(externalId)) {
//                        log.info("[청약홈] {}페이지 PBLANC_NO {} 이미 존재 - 저장 건너뜀", page, externalId);
//                        continue;
//                    }

                    repository.save(toEntity(item));
                }

                log.info("[청약홈] {}페이지 완료", page);
                page++;
            } catch(Exception e) {
                log.error("[청약홈] {}페이지 실패: {}", page, e.getMessage(), e);
                break;
            }
        } while ((page - 1) * PER_PAGE < totalCount);
    }

    private Announcement toEntity(ApplyhomeApiResponse.Item item) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = parseDate(item.getSubscrptRceptBgnde()); // 접수 시작일
        LocalDate endDate = parseDate(item.getSubscrptRceptEndde());   // 접수 종료일

        // 상태 판별 로직
        String finalStatus = "정보확인필요"; // 기본값

        if (endDate != null) {
            if (endDate.isBefore(today)) {
                finalStatus = "마감";
            } else if (startDate != null && !startDate.isAfter(today)) {
                finalStatus = "접수중";
            } else if (startDate != null && startDate.isAfter(today)) {
                finalStatus = "접수예정";
            }
        } else {
            // 종료일이 없는 경우 시작일이라도 지났으면 접수중으로 표시
            if (startDate != null && !startDate.isAfter(today)) {
                finalStatus = "접수중";
            }
        }

        return Announcement.builder()
                .externalId(item.getPblancNo())
                .sourceType("청약홈")
                .title(item.getHouseNm())
                .status(finalStatus)
                .region(item.getSubscrptAreaCodeNm())
                .address(item.getHssplyAdres())
                .recuitmentType(item.getHouseSecdNm())
                .targetType("공공임대주택")
                .sourceUrl(item.getPblancUrl())
                .supplyInstitution(item.getBsnsMbyNm())
                .totHshldCo(item.getTotSuplyHshldco() != null
                        ? String.valueOf(item.getTotSuplyHshldco()) : null)
                .beginDe(parseDate(item.getRcritPblancDe()))
                .applyStartDate(startDate)
                .applyEndDate(endDate)
                .endDe(parseDate(item.getPrzwnerPresnatnDe()))
                .isVisible(true)
                .build();
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            if (dateStr.contains("-")) {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            return null;
        }
    }
}
