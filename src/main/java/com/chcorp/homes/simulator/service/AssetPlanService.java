package com.chcorp.homes.simulator.service;

import com.chcorp.homes.simulator.dto.AssetPlanRequestDto;
import com.chcorp.homes.simulator.dto.AssetPlanResponseDto;
import com.chcorp.homes.simulator.entity.AssetPlan;
import com.chcorp.homes.simulator.repository.AssetPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.chcorp.homes.simulator.exception.PlanAccessDeniedException;
import com.chcorp.homes.simulator.exception.PlanNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetPlanService {

    private final AssetPlanRepository assetPlanRepository;

    // 플랜 전체 조회
    @Transactional(readOnly = true)
    public List<AssetPlanResponseDto> getPlans(Long userId) {
        return assetPlanRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(AssetPlanResponseDto::from)
                .toList();
    }

    // 플랜 생성
    @Transactional
    public AssetPlanResponseDto createPlan(Long userId, AssetPlanRequestDto dto) {
        AssetPlan plan = AssetPlan.builder()
                .userId(userId)
                .category(dto.category())
                .planName(dto.planName())
                .baseAsset(dto.baseAsset())
                .goalAmount(dto.goalAmount())
                .startDate(dto.startDate())
                .endDate(dto.endDate())
                .monthlySaving(dto.monthlySaving())
                .isCompleted(dto.isCompleted() != null ? dto.isCompleted() : false)
                .build();
        return AssetPlanResponseDto.from(assetPlanRepository.save(plan));
    }

    // 플랜 수정
    @Transactional
    public AssetPlanResponseDto updatePlan(Long userId, Long planId, AssetPlanRequestDto dto) {
        AssetPlan plan = assetPlanRepository.findById(planId)
                .orElseThrow(PlanNotFoundException::new);

        if (!plan.getUserId().equals(userId)) {
            throw new PlanAccessDeniedException();
        }
        plan.update(
                dto.category(),
                dto.planName(),
                dto.baseAsset(),
                dto.goalAmount(),
                dto.startDate(),
                dto.endDate(),
                dto.monthlySaving(),
                dto.isCompleted()
        );
        return AssetPlanResponseDto.from(plan);
    }

    // 플랜 삭제
    @Transactional
    public void deletePlan(Long userId, Long planId) {
        AssetPlan plan = assetPlanRepository.findById(planId)
                .orElseThrow(PlanNotFoundException::new);

        if (!plan.getUserId().equals(userId)) {
            throw new PlanAccessDeniedException();
        }
        assetPlanRepository.delete(plan);
    }
}