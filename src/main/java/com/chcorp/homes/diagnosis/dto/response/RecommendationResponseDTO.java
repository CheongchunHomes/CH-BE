// diagnosis/dto/response/RecommendationResponseDTO.java
package com.chcorp.homes.diagnosis.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

/**
 * ─────────────────────────────────────────────
 * 제도 추천 응답 DTO
 * - 9개 제도 채점 결과를 점수 내림차순으로 담아 반환
 * ─────────────────────────────────────────────
 */
@Getter
@Builder
public class RecommendationResponseDTO {

    /* 9개 제도 채점 결과 (점수 내림차순 정렬) */
    private List<PolicyResultDTO> results;
}