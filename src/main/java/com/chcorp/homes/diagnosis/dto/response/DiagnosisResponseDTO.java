package com.chcorp.homes.diagnosis.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 자가진단 응답 DTO
 * - 프로필 진단 / 가상 진단 공통 사용
 */
@Getter
@Builder
public class DiagnosisResponseDTO {

    // ── 6개 자격 상태 ("충족" / "일부제한" / "보완필요" / "미충족") ──
    private String houselessStatus;
    private String ageStatus;
    private String incomeStatus;
    private String assetStatus;
    private String subscriptionStatus;
    private String dependentStatus;

    // ── 4개 종합점수 (0~100) ──
    private Integer subscriptionScore;   // 청약준비도
    private Integer publicRentalScore;   // 공공임대 적합도
    private Integer jeonseScore;         // 전세대출 가능성
    private Integer saleScore;           // 분양형 당첨 가능성

    // ── 코멘트 ──
    private String strengthComment;      // 현재 강점
    private String weaknessComment;      // 보완 필요
    private String improveComment;       // 개선 제안
    private String recommendComment;     // 추천 방향
}