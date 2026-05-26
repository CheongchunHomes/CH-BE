package com.chcorp.homes.simulator.dto;

/**
 * 시뮬레이터 리포트 저장 요청 DTO
 */
public record SimulatorReportRequestDto(

        Object assetSnapshot,   // 탭01 자산 플랜 스냅샷
        Object housingSnapshot, // 탭02 주거 비교 스냅샷
        Object loanSnapshot,    // 탭03 금융 체감 스냅샷
        Object scoreSnapshot,   // 제도 추천 점수 스냅샷 (고도화 예정)
        Object aiResult,        // AI 분석 결과
        String aiPrompt         // 분석 시 사용된 AI 프롬프트
) {}