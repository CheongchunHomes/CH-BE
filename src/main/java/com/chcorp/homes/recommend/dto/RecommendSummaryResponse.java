package com.chcorp.homes.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendSummaryResponse {

    private List<RecommendItemDTO> policies;
    private String desiredCity;

    }

