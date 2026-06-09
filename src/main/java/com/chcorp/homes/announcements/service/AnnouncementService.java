package com.chcorp.homes.announcements.service;

import com.chcorp.homes.announcements.dto.RentalHouseApiResponse;
import com.chcorp.homes.announcements.entity.Announcement;
import com.chcorp.homes.announcements.repository.AnnouncementQueryRepository;
import com.chcorp.homes.announcements.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
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
    private final AnnouncementQueryRepository announcementQueryRepository;

    @Value("${api.public.service-key}")
    private String serviceKey;

    // 마이홈포털 임대주택 API
    private static final String BASE_URL =
            "https://apis.data.go.kr/1613000/HWSPR02/rsdtRcritNtcList";

    // 마이홈포털 공공분양주택 API
    private static final String SALE_BASE_URL =
            "https://apis.data.go.kr/1613000/HWSPR02/ltRsdtRcritNtcList";

    private static final int NUM_OF_ROWS = 100;

    // ===================
    // 마이홈포털 임대주택 수집
    // ===================
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

                if (apiResponse == null
                        || apiResponse.getResponse() == null
                        || apiResponse.getResponse().getBody() == null) {
                    log.warn("[임대주택][{}] {}페이지 응답 body 없음", brtcCode, pageNo);
                    break;
                }

                RentalHouseApiResponse.Body body = apiResponse.getResponse().getBody();

                if (pageNo == 1) {
                    totalCount = parseIntOrZero(body.getTotalCount());
                    log.info("[임대주택][{}] 전체 {}건 수집 시작", brtcCode, totalCount);
                }

                List<RentalHouseApiResponse.Item> items = body.getItem();

                if (items == null || items.isEmpty()) {
                    log.info("[임대주택][{}] {}페이지 수집 종료 - 데이터 없음", brtcCode, pageNo);
                    break;
                }

                for (RentalHouseApiResponse.Item item : items) {
                    String pblancId = item.getPblancId();
                    Integer houseSn = item.getHouseSn();

                    if (pblancId == null || pblancId.isBlank() || houseSn == null) {
                        log.warn("[임대주택][{}] {}페이지 pblancId 또는 houseSn 없음 - 저장 건너뜀", brtcCode, pageNo);
                        continue;
                    }

                    String externalId = "RENTAL-" + pblancId + "-" + houseSn;

                    String sourceType = "마이홈포털-공공임대주택";

                    if (repository.existsBySourceTypeAndExternalId(sourceType, externalId)) {
                        log.info("[임대주택][{}] externalId={} 이미 존재 - 저장 건너뜀", brtcCode, externalId);
                        continue;
                    }

                    repository.save(toEntity(item));
                }

                log.info("[임대주택][{}] {}페이지 완료", brtcCode, pageNo);
                pageNo++;

            } catch (Exception e) {
                log.error("[임대주택][{}] {}페이지 실패: {}", brtcCode, pageNo, e.getMessage(), e);
                break;
            }

        } while ((pageNo - 1) * NUM_OF_ROWS < totalCount);
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

    // =======================
    // 마이홈포털 공공분양주택 수집
    // =======================
    @Transactional
    public void fetchSaleAnnouncements() {
        int pageNo = 1;
        int totalCount = 0;

        do {
            try {
                String url = SALE_BASE_URL
                        + "?serviceKey=" + serviceKey
                        + "&pageNo=" + pageNo
                        + "&numOfRows=" + NUM_OF_ROWS;

                ResponseEntity<RentalHouseApiResponse> response =
                        restTemplate.getForEntity(url, RentalHouseApiResponse.class);

                RentalHouseApiResponse apiResponse = response.getBody();

                if (apiResponse == null
                        || apiResponse.getResponse() == null
                        || apiResponse.getResponse().getBody() == null) {
                    log.warn("[공공분양주택] {}페이지 응답 body 없음", pageNo);
                    break;
                }

                RentalHouseApiResponse.Body body = apiResponse.getResponse().getBody();

                if (pageNo == 1) {
                    totalCount = parseIntOrZero(body.getTotalCount());
                    log.info("[공공분양주택] 전체 {}건 수집 시작", totalCount);
                }

                List<RentalHouseApiResponse.Item> items = body.getItem();

                if (items == null || items.isEmpty()) {
                    log.info("[공공분양주택] {} 페이지 수집 종료 - 데이터 없음", pageNo);
                    break;
                }

                for (RentalHouseApiResponse.Item item : items) {
                    String pblancId = item.getPblancId();
                    Integer houseSn = item.getHouseSn();

                    if (pblancId == null || pblancId.isBlank() || houseSn == null) {
                        log.warn("[공공분양주택] {} 페이지 pblancId 또는 houseSn 없음 - 저장 건너뜀", pageNo);
                        continue;
                    }

                    String externalId = "SALE-" + pblancId + "-" + houseSn;

                    String sourceType = "마이홈포털-공공분양주택";

                    if (repository.existsBySourceTypeAndExternalId(sourceType, externalId)) {
                        log.info("[공공분양주택] externalId={} 이미 존재 - 저장 건너뜀", externalId);
                        continue;
                    }

                    repository.save(toSaleEntity(item));
                }

                log.info("[공공분양주택] {}페이지 완료", pageNo);
                pageNo++;

            } catch (Exception e) {
                log.error("[공공분양주택] {}페이지 실패: {}", pageNo, e.getMessage(), e);
                break;
            }
        } while ((pageNo - 1) * NUM_OF_ROWS < totalCount);
    }

    // ================
    // Entity 변환 메서드
    // ================
    private Announcement toEntity(RentalHouseApiResponse.Item item) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = parseDate(item.getRcritPblancDe());
        LocalDate endDate = parseDate(item.getPrzwnerPresnatnDe());

        String finalStatus = item.getSttusNm();

        if (endDate != null) {
            if (endDate.isBefore(today)) {
                finalStatus = "마감";
            } else if (startDate != null && !startDate.isAfter(today)) {
                finalStatus = "접수중";
            } else if (startDate != null && startDate.isAfter(today)) {
                finalStatus = "접수예정";
            }
        }

        String externalId = "RENTAL-" + item.getPblancId() + "-" + item.getHouseSn();

        return Announcement.builder()
                .externalId(externalId)
                .sourceType("마이홈포털-공공임대주택")
                .title(item.getPblancNm())
                .region(item.getBrtcNm())
                .address(item.getFullAdres())
                .status(finalStatus)
                .recuitmentType(item.getHouseTyNm())
                .targetType("공공임대주택")
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

    private Announcement toSaleEntity(RentalHouseApiResponse.Item item) {
        LocalDate today = LocalDate.now();

        LocalDate noticeDate = parseDate(item.getRcritPblancDe());
        LocalDate startDate = parseDate(item.getBeginDe());
        LocalDate endDate = parseDate(item.getEndDe());

        String finalStatus = item.getSttusNm();

        if (endDate != null) {
            if (endDate.isBefore(today)) {
                finalStatus = "마감";
            } else if (startDate != null && !startDate.isAfter(today)) {
                finalStatus = "접수중";
            } else if (startDate != null && startDate.isAfter(today)) {
                finalStatus = "접수예정";
            }
        }

        String externalId = "SALE-" + item.getPblancId() + "-" + item.getHouseSn();

        return Announcement.builder()
                .externalId(externalId)
                .sourceType("마이홈포털-공공분양주택")
                .title(item.getPblancNm())
                .region(item.getBrtcNm())
                .address(item.getFullAdres())
                .status(finalStatus)
                .recuitmentType(item.getHouseTyNm())
                .targetType("공공분양주택")
                .sourceUrl(item.getPcUrl() != null && !item.getPcUrl().isBlank()
                        ? item.getPcUrl()
                        : item.getUrl())
                .supplyInstitution(item.getSuplyInsttNm())
                .totHshldCo(item.getSumSuplyCo() == null ? null : String.valueOf(item.getSumSuplyCo()))
                .rentGtn(item.getEnty())
                .mtRntchrg(item.getPrtpay())
                .surlus(item.getSurlus())
                .heatMthdNm(item.getHeatMthdNm())
                .beginDe(noticeDate)
                .endDe(endDate)
                .content(buildSaleContent(item))
                .isVisible(true)
                .applyStartDate(startDate)
                .applyEndDate(endDate)
                .przwnerPresnatnDe(parseDate(item.getPrzwnerPresnatnDe()))
                .build();
    }

    private String buildSaleContent(RentalHouseApiResponse.Item item) {
        StringBuilder sb = new StringBuilder();

        if (item.getHsmpNm() != null && !item.getHsmpNm().isBlank()) {
            sb.append("단지명: ").append(item.getHsmpNm()).append("\n");
        }

        if (item.getRefrnc() != null && !item.getRefrnc().isBlank()) {
            sb.append("문의처: ").append(item.getRefrnc()).append("\n");
        }

        if (item.getUrl() != null && !item.getUrl().isBlank()) {
            sb.append("상세 정보: ").append(item.getUrl()).append("\n");
        }

        return sb.toString().trim();
    }

    // ================
    // 목록조회
    // 사용자 화면에는 isVisible = true인 공고만 조회
    // QueryDSL로 조건 검색 + 페이지네이션 처리
    // ================
    @Transactional(readOnly = true)
    public Page<Announcement> getList(
            String region,
            String status,
            String keyword,
            String sourceType,
            String targetType,
            boolean deadlineSoon,
            String areaType,
            BigDecimal latitude,
            BigDecimal longitude,
            String locationFilter,
            int page,
            int size
    ) {
        return announcementQueryRepository.search(
                normalize(region),
                normalize(status),
                normalize(keyword),
                normalize(sourceType),
                normalize(targetType),
                deadlineSoon,
                latitude,
                longitude,
                normalize(locationFilter),
                normalize(areaType),
                page,
                size
        );
    }

    // ===============
    // 공통 유틸 메서드
    // ===============
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            return null;
        }
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

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.isBlank()) {
            return null;
        }

        return value;
    }

    private boolean containsIgnoreCase(String value, String keyword) {
        if (value == null || keyword == null) {
            return false;
        }

        return value.toLowerCase().contains(keyword.toLowerCase());
    }

    private boolean matchesKeyword(Announcement announcement, String keyword) {
        if (keyword == null) {
            return true;
        }

        return containsIgnoreCase(announcement.getTitle(), keyword)
                || containsIgnoreCase(announcement.getRegion(), keyword)
                || containsIgnoreCase(announcement.getAddress(), keyword)
                || containsIgnoreCase(announcement.getSupplyInstitution(), keyword)
                || containsIgnoreCase(announcement.getRecuitmentType(), keyword)
                || containsIgnoreCase(announcement.getTargetType(), keyword);
    }

    private boolean isDeadlineSoon(LocalDate applyEndDate, LocalDate today, LocalDate deadlineEnd) {
        if (applyEndDate == null) {
            return false;
        }

        return !applyEndDate.isBefore(today) && !applyEndDate.isAfter(deadlineEnd);
    }

    // ==============
    // 단건 조회
    // ==============
    @Transactional(readOnly = true)
    public long countTodayAnnouncements() {
        Long count = repository.countTodayVisibleAnnouncements(LocalDate.now());
        return count == null ? 0L : count;
    }

    @Transactional(readOnly = true)
    public Announcement getOne(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("공고를 찾을 수 없습니다."));
    }
}
