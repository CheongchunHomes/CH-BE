package com.chcorp.homes.simulator.dto;

/**
 * 시뮬레이터 리포트 조회 응답 DTO
 */
public record SimulatorReportResponseDto(
        Object assetSnapshot,   // 탭01 자산 플랜 스냅샷
        Object housingSnapshot, // 탭02 주거 비교 스냅샷
        Object loanSnapshot,    // 탭03 금융 체감 스냅샷
        Object scoreSnapshot,   // 제도 추천 점수 스냅샷
        Object aiResult         // AI 분석 결과
) {}