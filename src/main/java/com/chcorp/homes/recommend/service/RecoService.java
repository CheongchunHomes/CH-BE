package com.chcorp.homes.recommend.service;

import com.chcorp.homes.recommend.dto.RecommendItemDTO;
import com.chcorp.homes.recommend.dto.RecommendSummaryResponse;
import com.chcorp.homes.recommend.entity.Recoentity;
import com.chcorp.homes.recommend.repository.Recorepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecoService {

    private final Recorepository recorepository;

    //전체 조회
    public List <Recoentity> getAll() {
        return recorepository.findAll();
    }
    /** 파라미터 직접 넘겨서 필터링 */
    public List<Recoentity> recommend(int age, int income, String region) {
        return recorepository.findByActiveTrue().stream()
                .filter(p -> age >= p.getMinAge() && age <= p.getMaxAge())
                .filter(p -> income <= p.getMaxIncome())
                .filter(p -> p.getRegion().equals("전국") || p.getRegion().equals(region))
                .collect(Collectors.toList());
    }


    /** 단건 조회 */
    public Recoentity getById(Long id) {
        return recorepository.findById(id).orElseThrow();
    }

    /** 추가 */
    public Recoentity create(Recoentity entity) {
        return recorepository.save(entity);
    }

    /** 수정 */
    public Recoentity update(Long id, Recoentity updated) {
        Recoentity entity = getById(id);
        entity.setName(updated.getName());
        entity.setCategory(updated.getCategory());
        entity.setDescription(updated.getDescription());
        entity.setActive(updated.isActive());
        return recorepository.save(entity);
    }

    /** 삭제 */
    public void delete(Long id) {
        recorepository.deleteById(id);
    }

    public RecommendSummaryResponse getSummary(Long userId) {

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

            //TODO: 실제 연결 시 announcements/policies 테이블에서 필터링
            List<RecommendItemDTO> policies = List.of(
                    RecommendItemDTO.builder()
                            .name("행복주택 청년 계층")
                            .category("행복주택")
                            .description("19~39세 청년 대상 공공임대주택")
                            .matchScore(95)
                            .applyUrl("https://apply.1h.or.kr")
                            .minAge(19)
                            .maxAge(39)
                            .maxIncome(3700) // 만원 단위
                            .build(),
                    RecommendItemDTO.builder()
                            .name("청년 전세임대")
                            .category("공공임대")
                            .description("청년이 원하는 주택을 LH가 전세계약 후 재임대")
                            .matchScore(88)
                            .applyUrl("https://apply.1h.or.kr")
                            .minAge(19)
                            .maxAge(39)
                            .maxIncome(3700)
                            .build(),
                    RecommendItemDTO.builder()
                            .name("행복주택 사회초년생 계층")
                            .category("행복주택")
                            .description("사회초년생 대상 시세 60~80% 수준 임대")
                            .matchScore(80)
                            .applyUrl("https://apply.1h.or.kr")
                            .minAge(19)
                            .maxAge(39)
                            .maxIncome(5000)
                            .build(),
                    RecommendItemDTO.builder()
                            .name("청년 매입임대")
                            .category("공공임대")
                            .description("LH가 매입한 주택을 청년에게 저렴하게 임대")
                            .matchScore(75)
                            .applyUrl("https://apply.1h.or.kr")
                            .minAge(19)
                            .maxAge(39)
                            .maxIncome(3700)
                            .build()
            );

            //TODO: 실제 연결 시 loan_products 테이블 필터링
            List<RecommendItemDTO> loans = List.of(
                    RecommendItemDTO.builder()
                            .name("청년전용 버팀목전제사금대출")
                            .category("대출")
                            .description("만 19~34세 청년, 최대 2억원, 금리 2%대")
                            .matchScore(92)
                            .applyUrl("https://nhuf.molit.go.kr")
                            .minAge(19)
                            .maxAge(39)
                            .maxIncome(5000)
                            .build(),
                    RecommendItemDTO.builder()
                            .name("중소기업취업청년 전월세보증금대출")
                            .category("대출")
                            .description("중소기업 재직 청년, 최대 1억원, 금리 1%대")
                            .matchScore(85)
                            .applyUrl("https://nhuf.molit.go.kr")
                            .minAge(19)
                            .maxAge(39)
                            .maxIncome(3500)
                            .build(),
                    RecommendItemDTO.builder()
                            .name("청년전용 보증부월세대출")
                            .category("대출")
                            .description("보증금 최대 3500만원, 월세 최대 40만원")
                            .matchScore(78)
                            .applyUrl("https://nhuf.molit.go.kr")
                            .minAge(19)
                            .maxAge(39)
                            .maxIncome(3500)
                            .build()
            );

            List<RecommendItemDTO> allItems = new ArrayList<>();
            allItems.addAll(policies);
            allItems.addAll(loans);

            return  RecommendSummaryResponse.builder()
                    .policies(allItems)
                    .diagnosis(diagnosis)
                    .build();
    }
}



