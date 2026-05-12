package com.chcorp.homes.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendSummaryResponse {

    private List<RecommendItemDTO> policies;
    private DiagnosisResult diagnosis;  // 더미데이터(진단 점수)용

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DiagnosisResult {
        private int subscriptionReadinessScore;
        private int publicRentalFitScore;
        private int jeonseloanScore;
        private int saleSubscriptionScore;
        private String subscriptionReadinessGrade;
        private String publicRentalFitGrade;
        private String jeonseloanGrade;
        private String saleSubscriptionGrade;

    }


}
