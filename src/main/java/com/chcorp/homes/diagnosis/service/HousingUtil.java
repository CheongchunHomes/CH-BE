// diagnosis/service/HousingUtil.java
package com.chcorp.homes.diagnosis.service;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

/**
 * ─────────────────────────────────────────────
 * 주거 진단/추천 공통 계산 유틸
 * - DiagnosisCalculator / RecommendationService 공용
 * ─────────────────────────────────────────────
 */
@Component
public class HousingUtil {

    // ── 소득 기준 상수 ──────────────────────────────
    private static final long INCOME_1P = 3_482_964L;
    private static final long INCOME_2P = 5_415_712L;
    private static final long INCOME_3P = 6_653_000L;
    private static final long INCOME_4P = 7_622_000L;

    // ── 자산 기준 상수 ──────────────────────────────
    private static final long ASSET_LIMIT = 361_000_000L;  // 3.61억

    /* 만 나이 계산 */
    public int calcAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /* 소득 기준 충족 여부 */
    public boolean checkIncome(Long annualIncome, Integer dependentCount) {
        if (annualIncome == null) return false;
        long monthly = annualIncome / 12;
        int count = dependentCount == null ? 0 : dependentCount;
        long limit = switch (count + 1) {
            case 1  -> INCOME_1P;
            case 2  -> INCOME_2P;
            case 3  -> INCOME_3P;
            default -> INCOME_4P;
        };
        return monthly <= limit;
    }

    /* 자산 기준 충족 여부 */
    public boolean checkAsset(Long totalAsset) {
        if (totalAsset == null) return false;
        return totalAsset <= ASSET_LIMIT;
    }
}