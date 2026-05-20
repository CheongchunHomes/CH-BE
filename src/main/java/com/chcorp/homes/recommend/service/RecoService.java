package com.chcorp.homes.recommend.service;

import com.chcorp.homes.announcements.entity.Announcement;
import com.chcorp.homes.announcements.repository.AnnouncementRepository;
import com.chcorp.homes.recommend.dto.RecommendItemDTO;
import com.chcorp.homes.recommend.dto.RecommendSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecoService {

    private final AnnouncementRepository announcementRepository;

    private static final List<String> KEYWORDS_HAENGBOK = List.of("행복주택");

    // 지정한 9개 제도로 변경해야함
    private static final List<String> KEYWORDS_PUBLIC_RENTAL = List.of(
            "매입임대",
            "전세임대",
            "신혼·신생아",
            "다자녀",
            "국민임대",
            "영구임대",
            "든든주택"
    );

    // announcements 테이블 기반 제도 필터링
    private List<RecommendItemDTO> recommendPolicies(String region) {
        Pageable pageable = PageRequest.of(0, 1020); // 첫페이지 부터, 한 페이지에 200개까지 가져옴, 추후 DB쿼리 단에서 필터링으로 개선
        List<Announcement> visible = announcementRepository.findByIsVisibleTrue(pageable).getContent();

        // 지역 필터 공통 적용
        List<Announcement> regionFiltered = visible.stream()
                .filter(a -> a.getRegion() == null
                        || a.getRegion().contains("전국")
                        || a.getRegion().contains(region))
                .filter(a -> "접수중".equals(a.getStatus()) || "접수예정".equals(a.getStatus()))
                .toList();

        // 행복주택
        List<RecommendItemDTO> haengbok = regionFiltered.stream()
                .filter(a -> KEYWORDS_HAENGBOK.stream()
                        .anyMatch(k -> a.getTitle() != null && a.getTitle().contains(k)))
                .limit(3)
                .map(a -> toDTO(a, "행복주택"))
                .toList();

        // 공공임대
        List<RecommendItemDTO> publicRental = regionFiltered.stream()
                .filter(a -> KEYWORDS_PUBLIC_RENTAL.stream()
                        .anyMatch(k -> a.getTitle() !=null && a.getTitle().contains(k)))
                .limit(3)
                .map(a -> toDTO(a, "공공임대"))
                .toList();

        List<RecommendItemDTO> result = new ArrayList<>();
        result.addAll(haengbok);
        result.addAll(publicRental);
        return result;

    }

    private RecommendItemDTO toDTO(Announcement a, String category) {
        return RecommendItemDTO.builder()
                .name(a.getTitle())
                .category(category)
                .description(a.getContent() != null
                    ? a.getContent().substring(0, Math.min(100, a.getContent().length()))
                        :"")
                .matchScore(80)
                .applyUrl(a.getSourceUrl())
                .build();
    }

    public RecommendSummaryResponse getSummary(Long userId) {

        // TODO: user_diagnosis_results 연결 후 실제 데이터로 교체
        RecommendSummaryResponse.DiagnosisResult diagnosis = RecommendSummaryResponse.DiagnosisResult.builder()
                .subscriptionReadinessScore(72)
                .publicRentalFitScore(85)
                .jeonseloanScore(90)
                .saleSubscriptionScore(45)
                .subscriptionReadinessGrade("B")
                .publicRentalFitGrade("A")
                .jeonseloanGrade("A")
                .saleSubscriptionGrade("C")
                .build();

        // TODO: user_profiles 연결 후 실제 지역으로 교체
        String region = "경기도";

        // announcements 테이블에서 제도 조회
        List<RecommendItemDTO> policies = recommendPolicies(region);

        // TODO: loan_products Repository 생기면 교체
        List<RecommendItemDTO> loans = List.of(
                RecommendItemDTO.builder()
                        .name("신생아 특례 버팀목대출")
                        .category("대출")
                        .description("신생아 가구 대상, 최대 3억원, 금리 1%대")
                        .matchScore(92)
                        .applyUrl("https://nhuf.molit.go.kr")
                        .build(),
                RecommendItemDTO.builder()
                        .name("청년전용 버팀목전세자금대출")
                        .category("대출")
                        .description("만 19~34세 청년, 최대 2억원, 금리 2%대")
                        .matchScore(88)
                        .applyUrl("https://nhuf.molit.go.kr")
                        .build(),
                RecommendItemDTO.builder()
                        .name("중소기업취업청년 전월세보증금대출")
                        .category("대출")
                        .description("중소기업 재직 청년, 최대 1억원, 금리 1%대")
                        .matchScore(85)
                        .applyUrl("https://nhuf.molit.go.kr")
                        .build(),
                RecommendItemDTO.builder()
                        .name("신혼부부전용 전세자금대출")
                        .category("대출")
                        .description("신혼부부 대상, 최대 2억원")
                        .matchScore(80)
                        .applyUrl("https://nhuf.molit.go.kr")
                        .build(),
                RecommendItemDTO.builder()
                        .name("청년전용 보증부월세대출")
                        .category("대출")
                        .description("보증금 최대 3500만원, 월세 최대 40만원")
                        .matchScore(75)
                        .applyUrl("https://nhuf.molit.go.kr")
                        .build(),
                RecommendItemDTO.builder()
                        .name("일반 버팀목 전세자금대출")
                        .category("대출")
                        .description("무주택 세대주 대상 전세자금 지원")
                        .matchScore(70)
                        .applyUrl("https://nhuf.molit.go.kr")
                        .build()
        );

        List<RecommendItemDTO> allItems = new ArrayList<>();
        allItems.addAll(policies);
        allItems.addAll(loans);

        return RecommendSummaryResponse.builder()
                .policies(allItems)
                .diagnosis(diagnosis)
                .build();
    }
}