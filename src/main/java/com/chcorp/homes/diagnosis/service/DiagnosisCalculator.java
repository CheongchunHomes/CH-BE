package com.chcorp.homes.diagnosis.service;

import com.chcorp.homes.diagnosis.dto.request.DiagnosisRequestDTO;
import com.chcorp.homes.diagnosis.dto.response.DiagnosisResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 진단 계산 컴포넌트
 * - 프로필 진단 / 가상 진단 공용
 * - 6개 자격 상태 + 4개 종합점수 + 코멘트 계산
 * - DB 저장 없음, 순수 계산만
 */
@Component
@RequiredArgsConstructor
public class DiagnosisCalculator {

    private final HousingUtil housingUtil;

    // ── 정책 기준(청년 나이, 청약 기간) ──────────────────────────────
    private static final int AGE_MIN          = 19;
    private static final int AGE_MAX          = 39;
    private static final int SUBSCRIPTION_1ST = 24; // 청약 1순위 기준 (개월)

    /**
     * 메인 계산 진입점
     * - DTO → 6개 자격상태 + 4개 점수 + 코멘트 → ResponseDTO
     */
    public DiagnosisResponseDTO calculate(DiagnosisRequestDTO dto) {
        int age = housingUtil.calcAge(dto.getBirthDate());

        return DiagnosisResponseDTO.builder()
                .houselessStatus(checkHouseless(dto.getHouseless()))
                .ageStatus(checkAge(age))
                .incomeStatus(checkIncomeStatus(dto.getAnnualIncome(), dto.getDependentCount()))
                .assetStatus(checkAssetStatus(dto.getTotalAsset()))
                .subscriptionStatus(checkSubscription(dto.getHasSubscription(), dto.getSubscriptionMonths()))
                .dependentStatus(checkDependent(dto.getDependentCount()))
                .subscriptionScore(calcSubscriptionScore(dto))
                .publicRentalScore(calcPublicRentalScore(dto))
                .jeonseScore(calcJeonseScore(dto))
                .saleScore(calcSaleScore(dto))
                .strengthComment(buildStrength(dto, age))
                .weaknessComment(buildWeakness(dto))
                .improveComment(buildImprove(dto))
                .recommendComment(buildRecommend(dto))
                .build();
    }

    // ── 6개 자격 상태 판별 ──────────────────────────

    // 무주택 여부 → 충족 / 미충족
    private String checkHouseless(Boolean houseless) {
        if (houseless == null) return "미충족";
        return houseless ? "충족" : "미충족";
    }

    // 만 나이 19~39세 → 충족 / 미충족
    private String checkAge(int age) {
        return (age >= AGE_MIN && age <= AGE_MAX) ? "충족" : "미충족";
    }

    // 소득 기준 → 충족 / 일부제한 / 미충족
    // 100% 이하 충족, 100~120% 일부제한 (맞벌이 가산 범위)
    private String checkIncomeStatus(Long annualIncome, Integer dependentCount) {
        if (annualIncome == null) return "미충족";
        if (housingUtil.checkIncome(annualIncome, dependentCount)) return "충족";
        long monthly = annualIncome / 12;
        if (monthly <= (long)(HousingUtil.INCOME_3P * 1.2))       return "일부제한";
        return "미충족";
    }

    // 자산 기준 → 충족 / 일부제한 / 미충족
    // 청년 기준(2억 5,100만원) 이하 충족, 10% 초과까지 일부제한
    // 차량 단독 기준(4,542만원)은 프론트 안내문구로 처리
    private String checkAssetStatus(Long totalAsset) {
        if (totalAsset == null) return "미충족";
        if (housingUtil.checkAsset(totalAsset, HousingUtil.ASSET_LIMIT_YOUTH))           return "충족";
        if (totalAsset <= (long)(HousingUtil.ASSET_LIMIT_YOUTH * 1.1))                   return "일부제한";
        return "미충족";
    }

    // 청약통장 → 충족 / 보완필요
    private String checkSubscription(Boolean has, Integer months) {
        if (has == null || !has)                           return "보완필요";
        if (months != null && months >= SUBSCRIPTION_1ST) return "충족";
        return "보완필요";
    }

    // 부양가족 수 → 충족 / 일부제한 / 보완필요
    private String checkDependent(Integer count) {
        if (count == null || count == 0) return "보완필요";
        if (count >= 3)                  return "충족";
        return "일부제한";
    }

    // ── 4개 종합점수 계산 (0~100점) ─────────────────

    /*
     * 청약준비도
     * 통장보유(40) + 가입기간 월수(최대40) + 무주택(20)
     */
    private int calcSubscriptionScore(DiagnosisRequestDTO dto) {
        int score = 0;
        if (Boolean.TRUE.equals(dto.getHasSubscription())) score += 40;
        if (dto.getSubscriptionMonths() != null)            score += Math.min(dto.getSubscriptionMonths(), 40);
        if (Boolean.TRUE.equals(dto.getHouseless()))        score += 20;
        return Math.min(score, 100);
    }

    /*
     * 공공임대 적합도
     * 무주택(40) + 소득기준 충족(30) / 3인기준 이하(15) + 자산기준 충족(30)
     */
    private int calcPublicRentalScore(DiagnosisRequestDTO dto) {
        int score = 0;
        if (Boolean.TRUE.equals(dto.getHouseless()))
            score += 40;
        if (housingUtil.checkIncome(dto.getAnnualIncome(), dto.getDependentCount()))
            score += 30;
        else if (dto.getAnnualIncome() != null
                && dto.getAnnualIncome() / 12 <= HousingUtil.INCOME_3P)
            score += 15;
        if (housingUtil.checkAsset(dto.getTotalAsset(), HousingUtil.ASSET_LIMIT_YOUTH))
            score += 30;
        return Math.min(score, 100);
    }

    /*
     * 전세대출 가능성
     * 기본(50) + 무주택(20) + 소득있음(20) + 현금자산있음(10)
     */
    private int calcJeonseScore(DiagnosisRequestDTO dto) {
        int score = 50;
        if (Boolean.TRUE.equals(dto.getHouseless()))                    score += 20;
        if (dto.getAnnualIncome() != null && dto.getAnnualIncome() > 0) score += 20;
        if (dto.getCashAsset() != null && dto.getCashAsset() > 0)       score += 10;
        return Math.min(score, 100);
    }

    /*
     * 분양형 당첨가능성
     * 청약가점 84점 만점 → 100점 환산
     * 무주택(20) + 부양가족수×5(최대35) + 청약기간/6(최대17)
     */
    private int calcSaleScore(DiagnosisRequestDTO dto) {
        int score = 0;
        score += calcHouselessScore(dto.getHouselessYears());
        if (dto.getDependentCount() != null)
            score += Math.min(dto.getDependentCount() * 5, 35);
        if (Boolean.TRUE.equals(dto.getHasSubscription()) && dto.getSubscriptionMonths() != null)
            score += Math.min(dto.getSubscriptionMonths() / 6, 17);
        return Math.min((int)(score / 84.0 * 100), 100);
    }

    private int calcHouselessScore(Integer years) {
        if (years == null || years == 0) return 0;
        if (years >= 15) return 32;
        return (years + 1) * 2;  // 1년→4점, 2년→6점 ... 14년→30점
    }

    // ── 코멘트 생성 ─────────────────────────────────

    private String buildStrength(DiagnosisRequestDTO dto, int age) {
        StringBuilder sb = new StringBuilder();
        if (Boolean.TRUE.equals(dto.getHouseless()))                        sb.append("무주택, ");
        if (age >= AGE_MIN && age <= AGE_MAX)                               sb.append("연령, ");
        if (dto.getAnnualIncome() != null
                && dto.getAnnualIncome() / 12 <= HousingUtil.INCOME_3P)    sb.append("소득 ");
        if (sb.isEmpty()) return "추가 조건 충족 시 더 많은 제도 이용이 가능합니다.";
        return sb.append("요건을 충족하여 다양한 주거 지원 제도 이용이 가능합니다.").toString();
    }

    private String buildWeakness(DiagnosisRequestDTO dto) {
        StringBuilder sb = new StringBuilder();
        if (dto.getTotalAsset() != null
                && dto.getTotalAsset() > HousingUtil.ASSET_LIMIT_YOUTH)
            sb.append("자산 기준이 일부 제도에서 초과될 수 있으며, ");
        if (!Boolean.TRUE.equals(dto.getHasSubscription()))
            sb.append("청약통장이 없습니다.");
        else if (dto.getSubscriptionMonths() == null || dto.getSubscriptionMonths() < SUBSCRIPTION_1ST)
            sb.append("청약통장 납입기간이 부족합니다.");
        return sb.isEmpty() ? "현재 대부분의 기준을 충족하고 있습니다." : sb.toString();
    }

    private String buildImprove(DiagnosisRequestDTO dto) {
        if (!Boolean.TRUE.equals(dto.getHasSubscription()))
            return "청약통장을 개설하고 꾸준히 납입하면 더 많은 제도 이용이 가능해집니다.";
        if (dto.getSubscriptionMonths() == null || dto.getSubscriptionMonths() < SUBSCRIPTION_1ST)
            return "청약통장 납입기간을 " + SUBSCRIPTION_1ST + "개월 이상으로 늘려 1순위 자격을 갖춰보세요.";
        if (dto.getDependentCount() == null || dto.getDependentCount() == 0)
            return "부양가족 가점이 낮아 분양형보다 공공임대·전세대출 위주 전략을 추천합니다.";
        return "현재 자격 조건이 양호합니다. 희망 지역 청약 일정을 꾸준히 확인하세요.";
    }

    private String buildRecommend(DiagnosisRequestDTO dto) {
        boolean houseless  = Boolean.TRUE.equals(dto.getHouseless());
        boolean incomeOk   = dto.getAnnualIncome() != null
                && dto.getAnnualIncome() / 12 <= HousingUtil.INCOME_3P;
        boolean subOk      = Boolean.TRUE.equals(dto.getHasSubscription())
                && dto.getSubscriptionMonths() != null
                && dto.getSubscriptionMonths() >= SUBSCRIPTION_1ST;
        boolean noDependent = dto.getDependentCount() == null || dto.getDependentCount() == 0;

        if (houseless && incomeOk && subOk && noDependent)
            return "공공임대 및 전세대출 제도를 우선 검토하고, 청약 준비를 병행하는 전략을 추천합니다.";
        if (houseless && incomeOk)
            return "소득·무주택 조건을 충족해 공공임대 신청이 가능합니다. 청약통장 납입을 병행하세요.";
        if (subOk && !incomeOk)
            return "청약 준비는 잘 되어 있으나 소득 조건을 확인해보세요. 민간 전세 위주 전략도 검토해보세요.";
        return "민간 전세 또는 청약 분양 위주로 전략을 세워보세요.";
    }
}