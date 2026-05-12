package com.chcorp.homes.diagnosis.service;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

/**
 * 주거 진단/추천 공통 계산 유틸
 * - DiagnosisCalculator / RecommendationService 공용
 */
@Component
public class HousingUtil {

    // ── 소득 기준 상수 (2025년 도시근로자 월평균소득 기준, 2026년 LH 행복주택 적용) ──
    public static final long INCOME_1P = 3_813_363L;
    public static final long INCOME_2P = 5_866_270L;
    public static final long INCOME_3P = 8_168_429L;
    public static final long INCOME_4P = 8_802_202L;

    // ── 자산 기준 상수 (2026년 LH 행복주택 입주자격 기준) ──
    public static final long ASSET_LIMIT_STUDENT  = 108_000_000L; // 대학생 => 1억 800만원
    public static final long ASSET_LIMIT_YOUTH    = 251_000_000L; // 청년 => 2억 5,100만원
    public static final long ASSET_LIMIT_NATIONAL = 345_000_000L; // 국민임대·신혼부부·한부모 => 3억 4,500만원

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

    /* 자산 기준 충족 여부 - 제도별 기준값을 호출부에서 지정 */
    public boolean checkAsset(Long totalAsset, long limit) {
        if (totalAsset == null) return false;
        return totalAsset <= limit;
    }
}