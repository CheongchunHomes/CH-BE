package com.chcorp.homes.policies.service;

import com.chcorp.homes.policies.dto.PublicServiceApiResponse;
import com.chcorp.homes.policies.dto.PublicServiceDetailApiResponse;
import com.chcorp.homes.policies.dto.YouthPolicyApiResponse;
import com.chcorp.homes.policies.entity.Policy;
import com.chcorp.homes.policies.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolicyService {

    private final RestTemplate restTemplate;
    private final PolicyRepository repository;

    // 기존 공고 api와 행안부 공공서비스 api 인증키 동일
    @Value("${api.public.service-key}")
    private String serviceKey;

    // 청년정책 API 인증키
    @Value("${api.policy.youth.service-key}")
    private String youthPolicyServiceKey;

    // 청년정책 API
    private static final String YOUTH_POLICY_BASE_URL =
            "https://www.youthcenter.go.kr/go/ythip/getPlcy";

    // 행안부 대한민국 공공서비스 목록 API
    private static final String PUBLIC_SERVICE_LIST_BASE_URL =
            "https://api.odcloud.kr/api/gov24/v3/serviceList";

    // 행안부 대한민국 공공서비스 상세 API
    private static final String PUBLIC_SERVICE_DETAIL_BASE_URL =
            "https://api.odcloud.kr/api/gov24/v3/serviceDetail";

    private static final int NUM_OF_ROWS = 100;

    // =========================
    // 청년정책 API 수집
    // =========================
    @Transactional
    public void fetchYouthPolicies() {
        int pageNo = 1;
        int totalCount = 0;

        do {
            try {
                String url = YOUTH_POLICY_BASE_URL
                        + "?apiKeyNm=" + youthPolicyServiceKey
                        + "&pageNum=" + pageNo
                        + "&pageSize=" + NUM_OF_ROWS
                        + "&pageType=1"
                        + "&rtnType=json";


                ResponseEntity<YouthPolicyApiResponse> response =
                        restTemplate.getForEntity(url, YouthPolicyApiResponse.class);

                YouthPolicyApiResponse apiResponse = response.getBody();

                if (apiResponse == null
                        || apiResponse.getResult() == null
                        || apiResponse.getResult().getYouthPolicyList() == null
                        || apiResponse.getResult().getYouthPolicyList().isEmpty()) {
                    log.info("[청년정책] {}페이지 수집 종료 - 데이터 없음", pageNo);
                    break;
                }

                YouthPolicyApiResponse.Result result = apiResponse.getResult();

                if (pageNo == 1 && result.getPagging() != null) {
                    totalCount = result.getPagging().getTotCount() == null
                            ? 0
                            : result.getPagging().getTotCount();

                    log.info("[청년정책] 전체 {}건 수집 시작", totalCount);
                }

                List<YouthPolicyApiResponse.Item> items = result.getYouthPolicyList();

                if (items == null || items.isEmpty()) {
                    log.info("[청년정책] {}페이지 수집 종료 - 데이터 없음", pageNo);
                    break;
                }

                for (YouthPolicyApiResponse.Item item : items) {
                    String externalId = item.getPlcyNo();

                    if (externalId == null || externalId.isBlank()) {
                        log.warn("[청년정책] {}페이지 plcyNo 없음 - 저장 건너뜀", pageNo);
                        continue;
                    }

                    if (repository.existsBySourceTypeAndExternalId("청년정책API", externalId)) {
                        continue;
                    }

                    // 우리 서비스 주제와 관련 있는 정책만 저장
                    if (!isHousingRelatedYouthPolicy(item)) {
                        continue;
                    }

                    repository.save(toYouthPolicyEntity(item));
                }

                log.info("[청년정책] {}페이지 완료", pageNo);
                pageNo++;

            } catch (Exception e) {
                log.error("[청년정책] {}페이지 실패: {}", pageNo, e.getMessage(), e);
                break;
            }

        } while ((pageNo - 1) * NUM_OF_ROWS < totalCount);
    }

    // =========================
    // 행안부 공공서비스 API 수집
    // =========================
    @Transactional
    public void fetchPublicServices() {
        int pageNo = 1;
        int totalCount = 0;

        do {
            try {
                String url = PUBLIC_SERVICE_LIST_BASE_URL
                        + "?serviceKey=" + serviceKey
                        + "&page=" + pageNo
                        + "&perPage=" + NUM_OF_ROWS;

                ResponseEntity<PublicServiceApiResponse> response =
                        restTemplate.getForEntity(url, PublicServiceApiResponse.class);

                PublicServiceApiResponse apiResponse = response.getBody();

                if (apiResponse == null || apiResponse.getData() == null) {
                    log.warn("[공공서비스] {}페이지 응답 body 없음", pageNo);
                    break;
                }

                if (pageNo == 1) {
                    totalCount = apiResponse.getTotalCount() == null
                            ? 0
                            : apiResponse.getTotalCount();

                    log.info("[공공서비스] 전체 {}건 수집 시작", totalCount);
                }

                List<PublicServiceApiResponse.Item> items = apiResponse.getData();

                if (items == null || items.isEmpty()) {
                    log.info("[공공서비스] {}페이지 수집 종료 - 데이터 없음", pageNo);
                    break;
                }

                for (PublicServiceApiResponse.Item item : items) {
                    String externalId = item.getServiceId();

                    if (externalId == null || externalId.isBlank()) {
                        log.warn("[공공서비스] {}페이지 serviceId 없음 - 저장 건너뜀", pageNo);
                        continue;
                    }

                    if (repository.existsBySourceTypeAndExternalId("공공서비스API", externalId)) {
                        continue;
                    }

                    // 목록 단계에서 주거/자립 관련 후보만 상세조회
                    if (!isHousingRelatedPublicService(item)) {
                        continue;
                    }

                    PublicServiceDetailApiResponse.Item detail = fetchPublicServiceDetail(externalId);

                    if (detail == null) {
                        log.warn("[공공서비스] serviceId={} 상세조회 실패 - 저장 건너뜀", externalId);
                        continue;
                    }

                    repository.save(toPublicServiceEntity(item, detail));
                }

                log.info("[공공서비스] {}페이지 완료", pageNo);
                pageNo++;

            } catch (Exception e) {
                log.error("[공공서비스] {}페이지 실패: {}", pageNo, e.getMessage(), e);
                break;
            }

        } while ((pageNo - 1) * NUM_OF_ROWS < totalCount);
    }

    // 행안부 공공서비스 상세 조회
    private PublicServiceDetailApiResponse.Item fetchPublicServiceDetail(String serviceId) {
        try {
            String url = PUBLIC_SERVICE_DETAIL_BASE_URL
                    + "?serviceKey=" + serviceKey
                    + "&page=1"
                    + "&perPage=1"
                    + "&cond[서비스ID::EQ]=" + serviceId;
            log.info("[공공서비스 상세] 요청 URL={}", url);

            ResponseEntity<PublicServiceDetailApiResponse> response =
                    restTemplate.getForEntity(url, PublicServiceDetailApiResponse.class);

            PublicServiceDetailApiResponse apiResponse = response.getBody();

            if (apiResponse == null
                    || apiResponse.getData() == null
                    || apiResponse.getData().isEmpty()) {
                return null;
            }

            return apiResponse.getData().get(0);

        } catch (Exception e) {
            log.error("[공공서비스 상세] serviceId={} 조회 실패: {}", serviceId, e.getMessage(), e);
            return null;
        }
    }

    // =========================
    // Entity 변환 메서드
    // =========================

    private Policy toYouthPolicyEntity(YouthPolicyApiResponse.Item item) {
        String mainCategory = decideMainCategoryByText(
                item.getPlcyNm(),
                item.getPlcyKywdNm(),
                item.getPlcyExplnCn(),
                item.getPlcySprtCn(),
                item.getAddAplyQlfcCndCn()
        );

        String subCategory = decideSubCategoryByText(
                item.getPlcyNm(),
                item.getPlcyKywdNm(),
                item.getPlcyExplnCn(),
                item.getPlcySprtCn(),
                item.getAddAplyQlfcCndCn()
        );

        return Policy.builder()
                .externalId(item.getPlcyNo())
                .sourceType("청년정책API")
                .title(item.getPlcyNm())
                .region(extractRegionFromText(
                        item.getSprvsnInstCdNm(),
                        item.getOperInstCdNm(),
                        item.getRgtrUpInstCdNm(),
                        item.getRgtrHghrkInstCdNm()
                ))
                .mainCategory(mainCategory)
                .subCategory(subCategory)
                .originalCategory(item.getLclsfNm())
                .originalMiddleCategory(item.getMclsfNm())
                .keyword(item.getPlcyKywdNm())
                .summary(item.getPlcyExplnCn())
                .content(item.getPlcySprtCn())
                .targetDesc(item.getAddAplyQlfcCndCn())
                .excludedTarget(item.getPtcpPrpTrgtCn())
                .selectionCriteria(null)
                .applyMethod(item.getPlcyAplyMthdCn())
                .screeningMethod(item.getSrngMthdCn())
                .applyPeriod(item.getAplyYmd())
                .businessPeriod(firstNotBlank(
                        item.getBizPrdEtcCn(),
                        joinPeriod(item.getBizPrdBgngYmd(), item.getBizPrdEndYmd())
                ))
                .requiredDocuments(item.getSbmsnDcmntCn())
                .etc(item.getEtcMttrCn())
                .supervisingInstitution(item.getSprvsnInstCdNm())
                .operatingInstitution(item.getOperInstCdNm())
                .receptionOrg(null)
                .contact(null)
                .sourceUrl(firstNotBlank(item.getRefUrlAddr1(), item.getRefUrlAddr2()))
                .onlineApplyUrl(item.getAplyUrlAddr())
                .law(null)
                .supportType(null)
                .ageMin(parseInteger(item.getSprtTrgtMinAge()))
                .ageMax(parseInteger(item.getSprtTrgtMaxAge()))
                .incomeMin(parseLong(item.getEarnMinAmt()))
                .incomeMax(parseLong(item.getEarnMaxAmt()))
                .incomeDesc(item.getEarnEtcCn())
                .homelessRequired(containsAny(
                        joinText(item.getPlcyNm(), item.getPlcySprtCn(), item.getAddAplyQlfcCndCn()),
                        List.of("무주택")
                ))
                .isVisible(true)
                .status(decideStatus(item.getAplyYmd()))
                .build();
    }

    private Policy toPublicServiceEntity(
            PublicServiceApiResponse.Item listItem,
            PublicServiceDetailApiResponse.Item detail
    ) {
        String mainCategory = decideMainCategoryByText(
                listItem.getServiceName(),
                listItem.getServiceSummary(),
                listItem.getServiceCategory(),
                detail.getServicePurpose(),
                detail.getSupportContent(),
                detail.getTargetDesc()
        );

        String subCategory = decideSubCategoryByText(
                listItem.getServiceName(),
                listItem.getServiceSummary(),
                listItem.getServiceCategory(),
                detail.getServicePurpose(),
                detail.getSupportContent(),
                detail.getTargetDesc()
        );

        return Policy.builder()
                .externalId(listItem.getServiceId())
                .sourceType("공공서비스API")
                .title(firstNotBlank(detail.getServiceName(), listItem.getServiceName()))
                .region(extractRegionFromText(
                        listItem.getSupervisingInstitution(),
                        detail.getSupervisingInstitution()
                ))
                .mainCategory(mainCategory)
                .subCategory(subCategory)
                .originalCategory(listItem.getServiceCategory())
                .originalMiddleCategory(null)
                .keyword(null)
                .summary(firstNotBlank(listItem.getServiceSummary(), detail.getServicePurpose()))
                .content(detail.getSupportContent())
                .targetDesc(detail.getTargetDesc())
                .excludedTarget(null)
                .selectionCriteria(detail.getSelectionCriteria())
                .applyMethod(detail.getApplyMethod())
                .screeningMethod(null)
                .applyPeriod(detail.getApplyPeriod())
                .businessPeriod(null)
                .requiredDocuments(detail.getRequiredDocuments())
                .etc(null)
                .supervisingInstitution(firstNotBlank(
                        detail.getSupervisingInstitution(),
                        listItem.getSupervisingInstitution()
                ))
                .operatingInstitution(null)
                .receptionOrg(detail.getReceptionOrg())
                .contact(detail.getContact())
                .sourceUrl(listItem.getDetailUrl())
                .onlineApplyUrl(detail.getOnlineApplyUrl())
                .law(detail.getLaw())
                .supportType(firstNotBlank(detail.getSupportType(), listItem.getSupportType()))
                .ageMin(null)
                .ageMax(null)
                .incomeMin(null)
                .incomeMax(null)
                .incomeDesc(null)
                .homelessRequired(containsAny(
                        joinText(listItem.getServiceName(), detail.getTargetDesc(), detail.getSelectionCriteria()),
                        List.of("무주택")
                ))
                .isVisible(true)
                .status(decideStatus(detail.getApplyPeriod()))
                .build();
    }

    // =========================
    // 목록 조회
    // =========================
    @Transactional(readOnly = true)
    public Page<Policy> getList(
            String mainCategory,
            String subCategory,
            String keyword,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("policyId").descending());

        mainCategory = normalize(mainCategory);
        subCategory = normalize(subCategory);
        keyword = normalize(keyword);

        String finalMainCategory = mainCategory;
        String finalSubCategory = subCategory;
        String finalKeyword = keyword;

        List<Policy> filtered = repository
                .findByIsVisibleTrue(Sort.by("policyId").descending())
                .stream()
                .filter(p -> finalMainCategory == null || finalMainCategory.equals(p.getMainCategory()))
                .filter(p -> finalSubCategory == null || finalSubCategory.equals(p.getSubCategory()))
                .filter(p -> finalKeyword == null || matchesKeyword(p, finalKeyword))
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());

        List<Policy> pageContent =
                start >= filtered.size() ? List.of() : filtered.subList(start, end);

        return new PageImpl<>(pageContent, pageable, filtered.size());
    }

    // =========================
    // 단건 조회
    // =========================
    @Transactional(readOnly = true)
    public Policy getOne(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("지원제도를 찾을 수 없습니다."));
    }

    // =========================
    // 주거 관련 필터링
    // =========================
    private boolean isHousingRelatedYouthPolicy(YouthPolicyApiResponse.Item item) {
        return containsAny(
                joinText(
                        item.getPlcyNm(),
                        item.getPlcyKywdNm(),
                        item.getPlcyExplnCn(),
                        item.getPlcySprtCn(),
                        item.getAddAplyQlfcCndCn()
                ),
                List.of(
                        "주거", "주택", "월세", "전세", "임대", "보증금",
                        "보증", "융자", "대출", "이사비", "부동산", "자립"
                )
        );
    }

    private boolean isHousingRelatedPublicService(PublicServiceApiResponse.Item item) {
        return "주거·자립".equals(item.getServiceCategory()) ||
                containsAny(
                        joinText(
                                item.getServiceName(),
                                item.getServiceSummary(),
                                item.getSupportContent(),
                                item.getTargetDesc()
                        ),
                        List.of(
                                "주거", "주택", "월세", "전세", "임대", "보증금",
                                "보증", "융자", "대출", "이사비", "부동산", "자립"
                        )
                );
    }

    // =========================
    // 카테고리 분류
    // =========================
    private String decideMainCategoryByText(String... values) {
        String text = joinText(values);

        if (containsAny(text, List.of(
                "월세", "전세", "임대", "보증금", "주거급여",
                "이사비", "주택구입", "주택 구입", "융자", "대출"
        ))) {
            return "주거비지원";
        }

        if (containsAny(text, List.of(
                "교육", "상담", "공간", "자립", "생활안정", "부동산"
        ))) {
            return "기타지원";
        }

        return "기타사업";
    }

    private String decideSubCategoryByText(String... values) {
        String text = joinText(values);

        if (containsAny(text, List.of("월세"))) {
            return "월세지원";
        }

        if (containsAny(text, List.of("전세", "보증금", "보증"))) {
            return "보증금지원";
        }

        if (containsAny(text, List.of("주거급여"))) {
            return "주거급여";
        }

        if (containsAny(text, List.of("이사비", "이사"))) {
            return "이사비지원";
        }

        if (containsAny(text, List.of("주택구입", "주택 구입", "구입", "융자", "대출"))) {
            return "융자지원";
        }

        if (containsAny(text, List.of("교육", "부동산"))) {
            return "주거교육";
        }

        if (containsAny(text, List.of("상담"))) {
            return "상담지원";
        }

        if (containsAny(text, List.of("공간"))) {
            return "공간지원";
        }

        return "기타";
    }

    private String decideStatus(String applyPeriod) {
        String value = normalize(applyPeriod);

        if (value == null) {
            return "확인필요";
        }

        if (value.contains("상시")) {
            return "상시신청";
        }

        if (value.contains("공고에 따름") || value.contains("수시")) {
            return "확인필요";
        }

        return "신청가능";
    }

    // =========================
    // 공통 유틸 메서드
    // =========================
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

    private String firstNotBlank(String... values) {
        for (String value : values) {
            String normalized = normalize(value);

            if (normalized != null) {
                return normalized;
            }
        }

        return null;
    }

    private String joinPeriod(String start, String end) {
        String normalizedStart = normalize(start);
        String normalizedEnd = normalize(end);

        if (normalizedStart == null && normalizedEnd == null) {
            return null;
        }

        if (normalizedStart == null) {
            return normalizedEnd;
        }

        if (normalizedEnd == null) {
            return normalizedStart;
        }

        return normalizedStart + " ~ " + normalizedEnd;
    }

    private String joinText(String... values) {
        StringBuilder sb = new StringBuilder();

        for (String value : values) {
            String normalized = normalize(value);

            if (normalized != null) {
                sb.append(normalized).append(" ");
            }
        }

        return sb.toString();
    }

    private boolean containsAny(String value, List<String> keywords) {
        String normalized = normalize(value);

        if (normalized == null) {
            return false;
        }

        for (String keyword : keywords) {
            if (normalized.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    private boolean containsIgnoreCase(String value, String keyword) {
        if (value == null || keyword == null) {
            return false;
        }

        return value.toLowerCase().contains(keyword.toLowerCase());
    }

    private boolean matchesKeyword(Policy policy, String keyword) {
        if (keyword == null) {
            return true;
        }

        return containsIgnoreCase(policy.getTitle(), keyword)
                || containsIgnoreCase(policy.getRegion(), keyword)
                || containsIgnoreCase(policy.getMainCategory(), keyword)
                || containsIgnoreCase(policy.getSubCategory(), keyword)
                || containsIgnoreCase(policy.getOriginalCategory(), keyword)
                || containsIgnoreCase(policy.getKeyword(), keyword)
                || containsIgnoreCase(policy.getSummary(), keyword)
                || containsIgnoreCase(policy.getContent(), keyword)
                || containsIgnoreCase(policy.getTargetDesc(), keyword)
                || containsIgnoreCase(policy.getSupervisingInstitution(), keyword);
    }

    private String extractRegionFromText(String... values) {
        String text = joinText(values);

        if (text.contains("서울")) return "서울";
        if (text.contains("경기")) return "경기";
        if (text.contains("인천")) return "인천";
        if (text.contains("강원")) return "강원";
        if (text.contains("대전")) return "대전";
        if (text.contains("세종")) return "세종";
        if (text.contains("대구")) return "대구";
        if (text.contains("광주")) return "광주";
        if (text.contains("울산")) return "울산";
        if (text.contains("부산")) return "부산";
        if (text.contains("제주")) return "제주";
        if (text.contains("경북")) return "경북";
        if (text.contains("경남")) return "경남";
        if (text.contains("전북")) return "전북";
        if (text.contains("전남")) return "전남";
        if (text.contains("충북")) return "충북";
        if (text.contains("충남")) return "충남";

        return null;
    }

    private Integer parseInteger(String value) {
        String normalized = normalize(value);

        if (normalized == null) {
            return null;
        }

        try {
            return Integer.parseInt(normalized);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Long parseLong(String value) {
        String normalized = normalize(value);

        if (normalized == null) {
            return null;
        }

        try {
            return Long.parseLong(normalized);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
