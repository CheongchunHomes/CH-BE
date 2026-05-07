// diagnosis/dto/response/PolicyResultDTO.java
package com.chcorp.homes.diagnosis.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * ─────────────────────────────────────────────
 * 제도별 채점 결과 DTO
 * - 9개 제도 각각 이 형태로 반환
 * ─────────────────────────────────────────────
 */
@Getter
@Builder
public class PolicyResultDTO {

    /* 제도명 (예: 청년 매입임대, 행복주택 청년 계층) */
    private String policyName;

    /* recoentity PK  */
    private Long recoId;

    /* 총점 (0~100점) */
    private Integer score;

    /* 추천 등급 (적극추천 / 추천가능 / 조건부추천 / 추천어려움) */
    private String grade;

    /* 핵심 추천 이유 */
    private String reason;

    /* 제도 설명 (recoentity.description) */
    private String description;

    /* 신청 링크 (recoentity.applyUrl) */
    private String applyUrl;
}