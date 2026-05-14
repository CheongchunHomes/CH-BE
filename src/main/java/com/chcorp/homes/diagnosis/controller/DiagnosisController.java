package com.chcorp.homes.diagnosis.controller;

import com.chcorp.homes.diagnosis.dto.request.DiagnosisRequestDTO;
import com.chcorp.homes.diagnosis.dto.response.DiagnosisResponseDTO;
import com.chcorp.homes.diagnosis.dto.response.UserProfileResponseDTO;
import com.chcorp.homes.diagnosis.service.DiagnosisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * DiagnosisController
 * - /api/diagnosis/profile  : 프로필 진단 (DB 저장)
 * - /api/diagnosis/simulate : 가상 진단 (저장 없음)
 *    @AuthenticationPrincipal
 *    JWT 토큰에서 userId 자동 추출
 *    JWT 구현 방식에 따라 타입 확인 필요
 */
@RestController
@RequestMapping("/diagnosis")
@RequiredArgsConstructor
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    /**
     * POST /api/diagnosis/profile
     * 프로필 진단: UserProfile UPSERT + Diagnosis INSERT
     */

    @PostMapping("/profile")
    public ResponseEntity<DiagnosisResponseDTO> profileDiagnosis(
            Authentication authentication,
            @RequestBody DiagnosisRequestDTO request
    ) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(diagnosisService.profileDiagnosis(userId, request));
    }
    /**
     * POST /api/diagnosis/simulate
     * 가상 진단: 계산만, DB 저장 없음
     */
    @PostMapping("/simulate")
    public ResponseEntity<DiagnosisResponseDTO> simulate(
            @RequestBody DiagnosisRequestDTO request
    ) {
        // 가상 진단은 로그인 불필요 (userId 없음)
        return ResponseEntity.ok(diagnosisService.simulateDiagnosis(request));
    }

    /**
     * GET /api/diagnosis/me
     * 내 최신 진단 프로필 조회 (마이페이지 연동)
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDTO> getMyProfile(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(diagnosisService.getMyProfile(userId));
    }
}
