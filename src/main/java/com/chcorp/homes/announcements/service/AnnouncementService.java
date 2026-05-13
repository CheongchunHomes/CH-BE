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
import org.springframework.data.domain.PageImpl;

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

                    if (pblancId == null || pblancId.isBlank()) {
                        log.warn("[임대주택][{}] {}페이지 pblancId 또는 houseSn 없음 - 저장 건너뜀", brtcCode, pageNo);
                        continue;
                    }

                    String externalId = "RENTAL-" + pblancId + "-" + houseSn;

                    if (repository.existsByExternalId(externalId)) {
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

                    if (repository.existsByExternalId(externalId)) {
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
                .externalId("Rental=" + item.getPblancId() + "-" + item.getHouseSn())
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

        LocalDate noticeDate = parseDate(item.getRcritPblancDe()); //공고일
        LocalDate startDate = parseDate(item.getBeginDe()); //접수 시작일
        LocalDate endDate = parseDate(item.getEndDe()); //접수 종료일

        String finalStatus = item.getSttusNm(); //기본값은 API 제공값

        if (endDate != null) {
            if(endDate.isBefore(today)) {
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
    // ================
    @Transactional(readOnly = true)
    public Page<Announcement> getList(
            String region,
            String status,
            String keyword,
            String sourceType,
            String targetType,
            boolean deadlineSoon,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("applyEndDate").descending());

        region = normalize(region);
        status = normalize(status);
        keyword = normalize(keyword);
        sourceType = normalize(sourceType);
        targetType = normalize(targetType);

        LocalDate today = LocalDate.now();
        LocalDate deadlineEnd = today.plusDays(30);

        // 람다식에서 쓰기 위한 final 복사본
        String finalRegion = region;
        String finalStatus = status;
        String finalKeyword = keyword;
        String finalTargetType = targetType;
        boolean finalDeadlineSoon = deadlineSoon;
        LocalDate finalToday = today;
        LocalDate finalDeadlineEnd = deadlineEnd;

        // 공공임대주택 / 공공분양주택 분리 조회
        // targetType이 있으면 먼저 해당 분류만 가져오고,
        // 그 안에서 지역/상태/검색어/마감임박을 Java에서 필터링
        if (finalTargetType != null) {
            List<Announcement> filtered = repository
                    .findAllByTargetType(finalTargetType, Sort.by("applyEndDate").descending())
                    .stream()
                    .filter(a -> finalRegion == null || containsIgnoreCase(a.getRegion(), finalRegion))
                    .filter(a -> finalStatus == null || finalStatus.equals(a.getStatus()))
                    .filter(a -> finalKeyword == null || matchesKeyword(a, finalKeyword))
                    .filter(a -> !finalDeadlineSoon || isDeadlineSoon(a.getApplyEndDate(), finalToday, finalDeadlineEnd))
                    .toList();

            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), filtered.size());

            List<Announcement> pageContent =
                    start >= filtered.size() ? List.of() : filtered.subList(start, end);

            return new PageImpl<>(pageContent, pageable, filtered.size());
        }

        // 마감일 임박 + 검색어 + 지역 + 상태
        if (deadlineSoon && keyword != null && region != null && status != null) {
            return repository.searchByKeywordAndRegionAndStatusAndDeadline(
                    keyword, region, status, today, deadlineEnd, pageable
            );
        }

        // 마감일 임박 + 검색어 + 지역
        if (deadlineSoon && keyword != null && region != null) {
            return repository.searchByKeywordAndRegionAndDeadline(
                    keyword, region, today, deadlineEnd, pageable
            );
        }

        // 마감일 임박 + 검색어 + 상태
        if (deadlineSoon && keyword != null && status != null) {
            return repository.searchByKeywordAndStatusAndDeadline(
                    keyword, status, today, deadlineEnd, pageable
            );
        }

        // 마감일 임박 + 검색어
        if (deadlineSoon && keyword != null) {
            return repository.searchByKeywordAndDeadline(
                    keyword, today, deadlineEnd, pageable
            );
        }

        // 마감일 임박 + 지역 + 상태
        if (deadlineSoon && region != null && status != null) {
            return repository.findByRegionContainingIgnoreCaseAndStatusAndApplyEndDateBetween(
                    region, status, today, deadlineEnd, pageable
            );
        }

        // 마감일 임박 + 지역
        if (deadlineSoon && region != null) {
            return repository.findByRegionContainingIgnoreCaseAndApplyEndDateBetween(
                    region, today, deadlineEnd, pageable
            );
        }

        // 마감일 임박 + 상태
        if (deadlineSoon && status != null) {
            return repository.findByStatusAndApplyEndDateBetween(
                    status, today, deadlineEnd, pageable
            );
        }

        // 마감일 임박만
        if (deadlineSoon) {
            return repository.findByApplyEndDateBetween(today, deadlineEnd, pageable);
        }

        // 검색어 + 지역 + 상태
        if (keyword != null && region != null && status != null) {
            return repository.searchByKeywordAndRegionAndStatus(keyword, region, status, pageable);
        }

        // 검색어 + 지역
        if (keyword != null && region != null) {
            return repository.searchByKeywordAndRegion(keyword, region, pageable);
        }

        // 검색어 + 상태
        if (keyword != null && status != null) {
            return repository.searchByKeywordAndStatus(keyword, status, pageable);
        }

        // 검색어만
        if (keyword != null) {
            return repository.searchByKeyword(keyword, pageable);
        }

        // 지역 + 상태
        if (region != null && status != null) {
            return repository.findByRegionContainingIgnoreCaseAndStatus(region, status, pageable);
        }

        // 지역
        if (region != null) {
            return repository.findByRegionContainingIgnoreCase(region, pageable);
        }

        // 상태
        if (status != null) {
            return repository.findByStatus(status, pageable);
        }

        return repository.findAll(pageable);
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
    public Announcement getOne(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("공고를 찾을 수 없습니다."));
    }
}