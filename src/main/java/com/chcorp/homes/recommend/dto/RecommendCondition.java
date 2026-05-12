package com.chcorp.homes.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendCondition {

    //TODO: user_diagmosis_results 연결 후 실제 값으로 채워질 필드들
    private String homelessStatus;  //무주택 상태
    private String ageStatus;       //연령조건상태
    private String incomeStatus;    //소득조건상태
    private String assetStatus;     //자산 상태
    private String familyStatus;    //부양가족 상태
    private String subscriptionStatus; //청약통장 상태

    //점수들
    private int subscriptionReadinessScore;
    private int publicRentalFitScore;
    private int jeonseloanScore;
    private int saleSubscriptionScore;
}
