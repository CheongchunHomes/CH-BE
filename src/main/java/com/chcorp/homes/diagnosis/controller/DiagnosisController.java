package com.chcorp.homes.diagnosis.controller;

import com.chcorp.homes.diagnosis.dto.request.DiagnosisRequestDTO;
import com.chcorp.homes.diagnosis.dto.response.DiagnosisResponseDTO;
import com.chcorp.homes.diagnosis.dto.response.UserProfileResponseDTO;
import com.chcorp.homes.diagnosis.service.DiagnosisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * DiagnosisController
 * - POST /diagnosis/profile  : 프로필 진단 (DB 저장)
 * - GET  /diagnosis/profile  : 저장된 프로필 조회 (폼 복원용)
 * - POST /diagnosis/simulate : 가상 진단 (저장 없음, 비로그인 가능)
 */
@RestController
@RequestMapping("/diagnosis")
@RequiredArgsConstructor
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    /**
     * POST /diagnosis/profile
     * 프로필 진단: UserProfile UPSERT + Diagnosis 히스토리 INSERT
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
     * GET /diagnosis/profile
     * 저장된 프로필 조회
     * - 다시 진단하기 시 폼 복원용
     * - 추천 파트 프로필 기반 채점 시 활용
     * - 프로필 없으면 204 No Content 반환
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDTO> getMyProfile(
            Authentication authentication
    ) {
        Long userId = Long.valueOf(authentication.getName());
        return diagnosisService.getMyProfile(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    /**
     * POST /diagnosis/simulate
     * 가상 진단: 계산만, DB 저장 없음
     * - 비로그인 사용자도 사용 가능 (화이트리스트 등록됨)
     */
    @PostMapping("/simulate")
    public ResponseEntity<DiagnosisResponseDTO> simulate(
            @RequestBody DiagnosisRequestDTO request
    ) {
        return ResponseEntity.ok(diagnosisService.simulateDiagnosis(request));
    }
}