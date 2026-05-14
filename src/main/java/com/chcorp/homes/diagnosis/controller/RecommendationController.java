// diagnosis/controller/RecommendationController.java
package com.chcorp.homes.diagnosis.controller;

import com.chcorp.homes.diagnosis.dto.request.DiagnosisRequestDTO;
import com.chcorp.homes.diagnosis.dto.response.RecommendationResponseDTO;
import com.chcorp.homes.diagnosis.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 제도 추천 컨트롤러
 * - 채점 계산만, 비즈니스 로직은 RecommendationService 위임
 */
@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    /* POST /api/recommendation/calculate
     * 자가진단 입력값 받아 9개 제도 채점 결과 반환
     * DB 저장 없음
     */
    @PostMapping("/calculate")
    public ResponseEntity<RecommendationResponseDTO> calculate(
            @RequestBody DiagnosisRequestDTO dto) {
        return ResponseEntity.ok(recommendationService.calculate(dto));
    }
}