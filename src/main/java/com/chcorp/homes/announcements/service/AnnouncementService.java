package com.chcorp.homes.announcements.service;

import com.chcorp.homes.announcements.dto.RentalHouseApiResponse;
import com.chcorp.homes.announcements.entity.Announcement;
import com.chcorp.homes.announcements.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnnouncementService {

    private final RestTemplate restTemplate;
    private final AnnouncementRepository repository;

    @Value("${api.public.service-key}")
    private String serviceKey;

    private static final String BASE_URL =
            "https://apis.data.go.kr/1613000/HWSPR02/rsdtRcritNtcList";
    private static final int NUM_OF_ROWS = 100;

    @Transactional
    public void fetchAll(String brtcCode) {
        int pageNo = 1;
        int totalCount = 0;

        do {
            try {
                String url = BASE_URL
                        + "?serviceKey=" + serviceKey
                        + "&pageNo=" + pageNo
                        + "&numOfRows=" + NUM_OF_ROWS
                        + "&brtcCode=" + brtcCode;

                ResponseEntity<RentalHouseApiResponse> response =
                        restTemplate.getForEntity(url, RentalHouseApiResponse.class);

                RentalHouseApiResponse apiResponse = response.getBody();

                if (apiResponse == null || apiResponse.getResponse() == null || apiResponse.getResponse().getBody() == null) {
                    log.warn("[{}] {}페이지 응답 body 없음", brtcCode, pageNo);
                    break;
                }

                RentalHouseApiResponse.Body body = apiResponse.getResponse().getBody();

                if (pageNo == 1) {
                    totalCount = parseIntOrZero(body.getTotalCount());
                    log.info("[{}] 전체 {}건 수집 시작", brtcCode, totalCount);
                }

                List<RentalHouseApiResponse.Item> items = body.getItem();

                if (items == null || items.isEmpty()) {
                    log.info("[{}] {}페이지 수집 종료 - 데이터 없음", brtcCode, pageNo);
                    break;
                }

                for (RentalHouseApiResponse.Item item : items) {
                    String externalId = item.getPblancId();

                    if (externalId == null || externalId.isBlank()) {
                        log.warn("[{}] {}페이지 pblancId 없음 - 저장 건너뜀", brtcCode, pageNo);
                        continue;
                    }

//                    if (repository.existsByExternalId(externalId)) {
//                        continue;
//                    }

                    repository.save(toEntity(item));
                }

                log.info("[{}] {}페이지 완료", brtcCode, pageNo);
                pageNo++;

            } catch (Exception e) {
                log.error("[{}] {}페이지 실패: {}", brtcCode, pageNo, e.getMessage(), e);
                break;
            }

        } while ((pageNo - 1) * NUM_OF_ROWS < totalCount);
    }

    private int parseIntOrZero(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Transactional
    public void fetchAllRegions() {
        Map<String, String> regions = new LinkedHashMap<>();
        regions.put("11", "서울특별시");
        regions.put("26", "부산광역시");
        regions.put("27", "대구광역시");
        regions.put("28", "인천광역시");
        regions.put("29", "광주광역시");
        regions.put("30", "대전광역시");
        regions.put("31", "울산광역시");
        regions.put("36", "세종특별자치시");
        regions.put("41", "경기도");
        regions.put("43", "충청북도");
        regions.put("44", "충청남도");
        regions.put("46", "전라남도");
        regions.put("47", "경상북도");
        regions.put("48", "경상남도");
        regions.put("50", "제주특별자치도");
        regions.put("51", "강원특별자치도");
        regions.put("52", "전북특별자치도");

        regions.forEach((code, name) -> {
            log.info("=== {} 수집시작 ===", name);
            fetchAll(code);
        });
    }

    private Announcement toEntity(RentalHouseApiResponse.Item item) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = parseDate(item.getRcritPblancDe()); // 접수 시작일
        LocalDate endDate = parseDate(item.getPrzwnerPresnatnDe()); // 접수 종료일(당첨자 발표일 등)

        // 상태 판별 로직
        String finalStatus = item.getSttusNm(); // 기본값은 API 제공값

        if (endDate != null) {
            if (endDate.isBefore(today)) {
                finalStatus = "마감";
            } else if (startDate != null && !startDate.isAfter(today)) {
                finalStatus = "접수중";
            } else if (startDate != null && startDate.isAfter(today)) {
                finalStatus = "접수예정";
            }
        }

        return Announcement.builder()
                .externalId(item.getPblancId())
                .sourceType("마이홈포털")
                .title(item.getPblancNm())
                .region(item.getBrtcNm())
                .address(item.getFullAdres())
                .status(finalStatus)
                .recuitmentType(item.getHouseTyNm())
                .targetType(item.getHouseTyNm())
                .sourceUrl(item.getUrl())
                .supplyInstitution(item.getSuplyInsttNm())
                .totHshldCo(item.getTotHshldCo())
                .rentGtn(item.getRentGtn())
                .mtRntchrg(item.getMtRntchrg())
                .heatMthdNm(item.getHeatMthdNm())
                .beginDe(parseDate(item.getBeginDe()))
                .endDe(endDate)
                .content(item.getRefrnc())
                .isVisible(true)
                .applyStartDate(startDate)
                .applyEndDate(endDate)
                .build();
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public Page<Announcement> getList(String region, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (region != null && status != null)
            return repository.findByRegionAndStatus(region, status, pageable);
        if (region != null)
            return repository.findByRegion(region, pageable);
        if (status != null)
            return repository.findByStatus(status, pageable);

        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Announcement getOne(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("공고를 찾을 수 없습니다."));
    }
}