package com.chcorp.homes.announcements.service;

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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
// 공고 관련 Service
public class AnnouncementService {

    private final RestTemplate restTemplate;
    private final AnnouncementRepository repository;
    private final ObjectMapper objectMapper;

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

                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode body = root.path("response").path("body");

                if (pageNo == 1) {
                    totalCount = body.path("totalCount").asInt();
                    log.info("[{}] 전체 {}건 수집 시작", brtcCode, totalCount);
                }

                JsonNode items = body.path("item");

                // 데이터가 없을 때 종료되는 조건
                if (items.isMissingNode() || (items.isArray() && items.size() == 0)) {
                    log.info("{} 지역 수집 종료 (데이터 없음)", brtcCode);
                    break;
                }

                if (items.isArray()) {
                    for (JsonNode item : items) {
                        String externalId = item.path("pblanId").asText();
                        if (repository.existsByExternalId(externalId)) continue;
                        repository.save(toEntity(item));
                    }
                } else if(items.isObject()) {
                    //데이터가 딱 1개일 때는 배열이 아니라 객체로 올 수 있기 때문에 해당 내용 처리
                    String externalId = items.path("pblanId").asText();
                    if (!repository.existsByExternalId(externalId)) {
                        repository.save(toEntity(items));
                    }
                }

                log.info("{}페이지 완료", pageNo);
                pageNo++;
            } catch (Exception e) {
                log.error("{}페이지 실패: {}", pageNo, e.getMessage());
                break;
            }

        }   while((pageNo - 1) * NUM_OF_ROWS < totalCount);
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

    // API 응답 -> ERD 컬럼 매핑
    private Announcement toEntity(JsonNode item) {
        Announcement a = new Announcement();

        a.setExternalId(item.path("pblancId").asText(null));
        a.setSourceType("청약홈");
        a.setTitle(item.path("pblancNm").asText(null));
        a.setRegion(item.path("brtcNm").asText(null));
        a.setAddress(item.path("fullAdres").asText(null));
        a.setStatus(item.path("sttusNm").asText(null));
        a.setRecuitmentType(item.path("houseTyNm").asText(null));
        a.setTargetType(item.path("houseTyNm").asText(null));
        a.setSourceUrl(item.path("url").asText(null));
        a.setSupplyInstitution(item.path("suplyInsttNm").asText(null));
        a.setTotHshldCo(item.path("totHshldCo").asText(null));
        a.setRentGtn(item.path("rentGtn").asLong(0));
        a.setMtRntchrg(item.path("mtRntchrg").asLong(0));
        a.setHeatMthdNm(item.path("heatMtndNm").asText(null));
        a.setBeginDe(parseDate(item.path("beginDe").asText(null)));
        a.setEndDe(parseDate(item.path("endDe").asText(null)));
        a.setContent(item.path("refrnc").asText(null));
        a.setIsVisible(true);

        // 날짜 파싱 (yyyyMMdd 형식)
        a.setApplyStartDate(parseDate(item.path("rcritPblancDe").asText(null)));
        a.setApplyEndDate(parseDate(item.path("przwnerPresnatnDe").asText(null)));

        return a;
    }

    // "20260421" -> LocalDate
    private LocalDate parseDate(String dateStr) {
        if(dateStr == null || dateStr.isBlank()) return null;
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch(Exception e) {
            return null;
        }
    }

    // 프린트 요청용
    @Transactional(readOnly = true)
    public Page<Announcement> getList(String region, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if(region != null && status != null)
            return repository.findByRegionAndStatus(region, status, pageable);
        if(region != null)
            return repository.findByRegion(region, pageable);
        if(status != null)
            return repository.findByStatus(status, pageable);

        return repository.findAll(pageable);
    }

    // api 단건 조회 (공고 상세페이지 용)
    @Transactional(readOnly = true)
    public Announcement getOne(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("공고를 찾을 수 없습니다."));
    }
}
