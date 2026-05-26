package com.chcorp.homes.simulator.dto;

import java.time.LocalDate;

public record AssetPlanRequestDto(
        String category,
        String planName,
        Long baseAsset,
        Long goalAmount,
        LocalDate startDate,
        LocalDate endDate,
        Long monthlySaving,
        Boolean isCompleted
) {}