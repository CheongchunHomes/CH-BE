package com.chcorp.homes.simulator.dto;

import com.chcorp.homes.simulator.entity.AssetPlan;

import java.time.LocalDate;
import java.time.Instant;

public record AssetPlanResponseDto(
        Long planId,
        String category,
        String planName,
        Long baseAsset,
        Long goalAmount,
        LocalDate startDate,
        LocalDate endDate,
        Long monthlySaving,
        Boolean isCompleted,
        Instant createdAt
) {
    // 엔티티 → DTO 변환
    public static AssetPlanResponseDto from(AssetPlan plan) {
        return new AssetPlanResponseDto(
                plan.getPlanId(),
                plan.getCategory(),
                plan.getPlanName(),
                plan.getBaseAsset(),
                plan.getGoalAmount(),
                plan.getStartDate(),
                plan.getEndDate(),
                plan.getMonthlySaving(),
                plan.getIsCompleted(),
                plan.getCreatedAt()
        );
    }
}