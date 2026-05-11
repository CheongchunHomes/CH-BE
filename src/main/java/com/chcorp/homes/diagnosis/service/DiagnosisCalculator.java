package com.chcorp.homes.diagnosis.service;

import com.chcorp.homes.diagnosis.dto.request.DiagnosisRequestDTO;
import com.chcorp.homes.diagnosis.dto.response.DiagnosisResponseDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

/**
 * ─────────────────────────────────────────────
 * 진단 계산 공통 컴포넌트
 * - 프로필 진단 / 가상 진단 모두 이 클래스로 계산
 * - DB 저장 로직 없음 (순수 계산만)
 *
 *    @Component: Spring이 자동으로 관리하는 객체
 *    Service에서 주입받아 사용
 * ─────────────────────────────────────────────
 */
@Component
public class DiagnosisCalculator {

    // ── 정책 기준 상수 ──────────────────────────────
    private static final int  AGE_MIN          = 19;
    private static final int  AGE_MAX          = 39;
    private static final long ASSET_LIMIT      = 361_000_000L;  // 3.61억
    private static final int  SUBSCRIPTION_1ST = 24;            // 1순위 기준 개월
    private static final long INCOME_1P        = 3_482_964L;    // 1인 월소득 기준
    private static final long INCOME_2P        = 5_415_712L;
    private static final long INCOME_3P        = 6_653_000L;
    private static final long INCOME_4P        = 7_622_000L;

    /**
     * 메인 계산 메서드
     * - DTO 받아서 ResponseDTO 반환
     * - DB 저장 없음
     */
    public DiagnosisResponseDTO calculate(DiagnosisRequestDTO dto) {
        int age = calcAge(dto.getBirthDate());

        return DiagnosisResponseDTO.builder()
                // 6개 자격 상태
                .houselessStatus(checkHouseless(dto.getHouseless()))
                .ageStatus(checkAge(age))
                .incomeStatus(checkIncome(dto.getAnnualIncome(), dto.getDependentCount()))
                .assetStatus(checkAsset(dto.getTotalAsset()))
                .subscriptionStatus(checkSubscription(dto.getHasSubscription(), dto.getSubscriptionMonths()))
                .dependentStatus(checkDependent(dto.getDependentCount()))
                // 4개 점수
                .subscriptionScore(calcSubscriptionScore(dto))
                .publicRentalScore(calcPublicRentalScore(dto))
                .jeonseScore(calcJeonseScore(dto))
                .saleScore(calcSaleScore(dto))
                // 코멘트
                .strengthComment(buildStrength(dto, age))
                .weaknessComment(buildWeakness(dto))
                .improveComment(buildImprove(dto))
                .recommendComment(buildRecommend(dto))
                .build();
    }

    // ── 만 나이 계산 ────────────────────────────────
    private int calcAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    // ── 6개 자격 상태 판별 ──────────────────────────

    private String checkHouseless(Boolean houseless) {
        if (houseless == null) return "미충족";
        return houseless ? "충족" : "미충족";
    }

    private String checkAge(int age) {
        if (age >= AGE_MIN && age <= AGE_MAX) return "충족";
        return "미충족";
    }

    private String checkIncome(Long annualIncome, Integer dependentCount) {
        if (annualIncome == null) return "미충족";
        long monthly = annualIncome / 12;
        int count = dependentCount == null ? 0 : dependentCount;
        long limit = switch (count + 1) {
            case 1  -> INCOME_1P;
            case 2  -> INCOME_2P;
            case 3  -> INCOME_3P;
            default -> INCOME_4P;
        };
        if (monthly <= limit)        return "충족";
        if (monthly <= limit * 1.2)  return "일부제한";
        return "미충족";
    }

    private String checkAsset(Long totalAsset) {
        if (totalAsset == null) return "미충족";
        if (totalAsset <= ASSET_LIMIT)           return "충족";
        if (totalAsset <= (long)(ASSET_LIMIT * 1.1)) return "일부제한";
        return "미충족";
    }

    private String checkSubscription(Boolean has, Integer months) {
        if (has == null || !has)           return "보완필요";
        if (months != null && months >= SUBSCRIPTION_1ST) return "충족";
        return "보완필요";
    }

    private String checkDependent(Integer count) {
        if (count == null || count == 0) return "보완필요";
        if (count >= 3) return "충족";
        return "보통";
    }

    // ── 4개 종합점수 계산 (0~100) ───────────────────

    /* 청약준비도: 통장보유(40) + 가입기간(40) + 무주택(20) */
    private int calcSubscriptionScore(DiagnosisRequestDTO dto) {
        int score = 0;
        if (Boolean.TRUE.equals(dto.getHasSubscription()))  score += 40;
        if (dto.getSubscriptionMonths() != null) {
            score += Math.min(dto.getSubscriptionMonths() * 1, 40);
        }
        if (Boolean.TRUE.equals(dto.getHouseless()))         score += 20;
        return Math.min(score, 100);
    }

    /* 공공임대 적합도: 무주택(40) + 소득(30) + 자산(30) */
    private int calcPublicRentalScore(DiagnosisRequestDTO dto) {
        int score = 0;
        if (Boolean.TRUE.equals(dto.getHouseless()))             score += 40;
        if (dto.getAnnualIncome() != null) {
            long monthly = dto.getAnnualIncome() / 12;
            if (monthly <= INCOME_1P)      score += 30;
            else if (monthly <= INCOME_3P) score += 15;
        }
        if (dto.getTotalAsset() != null && dto.getTotalAsset() <= ASSET_LIMIT) score += 30;
        return Math.min(score, 100);
    }

    /* 전세대출 가능성: 기본(50) + 무주택(20) + 소득(20) + 현금(10) */
    private int calcJeonseScore(DiagnosisRequestDTO dto) {
        int score = 50;
        if (Boolean.TRUE.equals(dto.getHouseless()))   score += 20;
        if (dto.getAnnualIncome() != null && dto.getAnnualIncome() > 0) score += 20;
        if (dto.getCashAsset() != null && dto.getCashAsset() > 0)       score += 10;
        return Math.min(score, 100);
    }

    /* 분양형 당첨가능성: 청약가점 84점 만점 → 100점 환산 */
    private int calcSaleScore(DiagnosisRequestDTO dto) {
        int score = 0;
        if (Boolean.TRUE.equals(dto.getHouseless()))   score += 20;
        if (dto.getDependentCount() != null)            score += Math.min(dto.getDependentCount() * 5, 35);
        if (Boolean.TRUE.equals(dto.getHasSubscription()) && dto.getSubscriptionMonths() != null) {
            score += Math.min(dto.getSubscriptionMonths() / 6, 17);
        }
        return Math.min((int)(score / 84.0 * 100), 100);
    }

    // ── 코멘트 생성 ─────────────────────────────────

    private String buildStrength(DiagnosisRequestDTO dto, int age) {
        StringBuilder sb = new StringBuilder();
        if (Boolean.TRUE.equals(dto.getHouseless()))            sb.append("무주택, ");
        if (age >= AGE_MIN && age <= AGE_MAX)                   sb.append("연령, ");
        if (dto.getAnnualIncome() != null
                && dto.getAnnualIncome() / 12 <= INCOME_3P)    sb.append("소득 ");
        if (sb.isEmpty()) return "추가 조건 충족 시 더 많은 제도 이용이 가능합니다.";
        return sb.append("요건을 충족하여 다양한 주거 지원 제도 이용이 가능합니다.").toString();
    }

    private String buildWeakness(DiagnosisRequestDTO dto) {
        StringBuilder sb = new StringBuilder();
        if (dto.getTotalAsset() != null && dto.getTotalAsset() > ASSET_LIMIT)
            sb.append("자산 기준이 일부 제도에서 초과될 수 있으며, ");
        if (dto.getSubscriptionMonths() == null || dto.getSubscriptionMonths() < SUBSCRIPTION_1ST)
            sb.append("청약통장 가입기간이 짧습니다.");
        return sb.isEmpty() ? "현재 대부분의 기준을 충족하고 있습니다." : sb.toString();
    }

    private String buildImprove(DiagnosisRequestDTO dto) {
        if (dto.getSubscriptionMonths() == null || dto.getSubscriptionMonths() < SUBSCRIPTION_1ST) {
            return "청약통장 가입기간을 늘리고, 자산 관리를 통해 더 많은 제도 이용 가능성을 높여보세요.";
        }
        return "현재 자격 조건이 양호합니다. 희망 지역 청약 일정을 꾸준히 확인하세요.";
    }

    private String buildRecommend(DiagnosisRequestDTO dto) {
        if (Boolean.TRUE.equals(dto.getHouseless())
                && dto.getAnnualIncome() != null
                && dto.getAnnualIncome() / 12 <= INCOME_3P) {
            return "공공임대 및 전세대출 제도를 우선 검토하고, 청약 준비를 병행하는 전략을 추천합니다.";
        }
        return "민간 전세 또는 청약 분양 위주로 전략을 세워보세요.";
    }
}