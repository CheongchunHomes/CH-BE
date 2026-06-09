package com.chcorp.homes.simulator.controller;

import com.chcorp.homes.simulator.dto.AssetPlanRequestDto;
import com.chcorp.homes.simulator.dto.AssetPlanResponseDto;
import com.chcorp.homes.simulator.service.AssetPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/simulator/asset-plans")
@RequiredArgsConstructor
public class AssetPlanController {

    private final AssetPlanService assetPlanService;

    // GET /api/simulator/asset-plans — 플랜 전체 조회
    @GetMapping
    public ResponseEntity<List<AssetPlanResponseDto>> getPlans(Authentication authentication) {
        if (authentication == null) return ResponseEntity.ok(List.of());
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(assetPlanService.getPlans(userId));
    }

    // POST /api/simulator/asset-plans — 플랜 생성
    @PostMapping
    public ResponseEntity<AssetPlanResponseDto> createPlan(
            Authentication authentication,
            @RequestBody AssetPlanRequestDto dto
    ) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(assetPlanService.createPlan(userId, dto));
    }

    // PUT /api/simulator/asset-plans/:id — 플랜 수정
    @PutMapping("/{planId}")
    public ResponseEntity<AssetPlanResponseDto> updatePlan(
            Authentication authentication,
            @PathVariable Long planId,
            @RequestBody AssetPlanRequestDto dto
    ) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(assetPlanService.updatePlan(userId, planId, dto));
    }

    // DELETE /api/simulator/asset-plans/:id — 플랜 삭제
    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deletePlan(
            Authentication authentication,
            @PathVariable Long planId
    ) {
        Long userId = Long.valueOf(authentication.getName());
        assetPlanService.deletePlan(userId, planId);
        return ResponseEntity.noContent().build();
    }
}