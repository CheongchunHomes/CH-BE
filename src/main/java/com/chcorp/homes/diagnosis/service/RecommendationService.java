package com.chcorp.homes.diagnosis.service;

import com.chcorp.homes.diagnosis.dto.request.DiagnosisRequestDTO;
import com.chcorp.homes.diagnosis.dto.response.PolicyResultDTO;
import com.chcorp.homes.diagnosis.dto.response.RecommendationResponseDTO;
import com.chcorp.homes.diagnosis.entity.EmploymentPeriod;
import com.chcorp.homes.diagnosis.entity.EmploymentStatus;
import com.chcorp.homes.diagnosis.entity.MarriagePeriod;
import com.chcorp.homes.recommend_test.entity.Recoentity;
import com.chcorp.homes.recommend_test.repository.Recorepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * 제도 추천 채점 서비스
 *
 * [역할]
 * - 자가진단 결과값을 받아 9개 제도 각각 100점 만점으로 채점
 * - 점수 높은 순으로 정렬해서 반환
 * - DB 저장 없음, 계산 후 즉시 반환
 *
 * [채점 구조]
 * - 각 제도마다 판단 항목별 배점 합산
 * - 총점 기준으로 추천 등급 결정
 *   90점↑ 적극추천 / 70점↑ 추천가능 / 50점↑ 조건부추천 / 50점↓ 추천어려움
 *
 * [의존]
 * - HousingUtil: 나이계산, 소득기준, 자산기준 공통 계산
 * - Recorepository: 제도 상세정보 조회 (description, applyUrl)
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final HousingUtil housingUtil;
    private final Recorepository recorepository;

    // ── 연령 기준 상수 ──────────────────────────────
    private static final int AGE_MIN = 19;  // 청년 최소 나이
    private static final int AGE_MAX = 39;  // 청년 최대 나이

    // ── 추천 등급 기준 상수 ─────────────────────────
    private static final int GRADE_ACTIVE   = 90;  // 적극추천 기준점
    private static final int GRADE_POSSIBLE = 70;  // 추천가능 기준점
    private static final int GRADE_CHECK    = 50;  // 조건부추천 기준점

    /**
     * 메인 채점 메서드
     * 1. HousingUtil로 나이/소득/자산 공통 계산
     * 2. 9개 제도 각각 채점
     * 3. 점수 내림차순 정렬 후 반환
     *
     * @param dto 자가진단 입력값
     * @return 9개 제도 채점 결과 (점수 내림차순)
     */
    public RecommendationResponseDTO calculate(DiagnosisRequestDTO dto) {
        int age            = housingUtil.calcAge(dto.getBirthDate());
        boolean incomePass        = housingUtil.checkIncome(dto.getAnnualIncome(), dto.getDependentCount());
        boolean assetPassStudent  = housingUtil.checkAsset(dto.getTotalAsset(), HousingUtil.ASSET_LIMIT_STUDENT);
        boolean assetPassYouth    = housingUtil.checkAsset(dto.getTotalAsset(), HousingUtil.ASSET_LIMIT_YOUTH);
        boolean assetPassNational = housingUtil.checkAsset(dto.getTotalAsset(), HousingUtil.ASSET_LIMIT_NATIONAL);

        // DB에서 활성 제도 전체 조회 → name 기준 Map 캐싱 (N+1 제거)
        Map<String, Recoentity> recoMap = recorepository.findByActiveTrue()
                .stream()
                .collect(Collectors.toMap(Recoentity::getName, r -> r));

        List<PolicyResultDTO> sorted = Stream.of(
                        scoreYouthPurchase(dto, age, incomePass, assetPassNational, recoMap),
                        scoreYouthJeonse(dto, age, incomePass, assetPassNational, recoMap),
                        scoreHappyStudent(dto, incomePass, assetPassStudent, recoMap),
                        scoreHappyJobSeeker(dto, incomePass, assetPassYouth, recoMap),
                        scoreHappyYouth(dto, age, incomePass, assetPassYouth, recoMap),
                        scoreHappyNewcomer(dto, incomePass, assetPassYouth, recoMap),
                        scoreHappyNewlywed(dto, incomePass, assetPassNational, recoMap),
                        scoreHappyPreNewlywed(dto, incomePass, assetPassNational, recoMap),
                        scoreHappySingleParent(dto, incomePass, assetPassNational, recoMap)
                )
                .sorted(Comparator.comparingInt(PolicyResultDTO::getScore).reversed())
                .toList();

        return RecommendationResponseDTO.builder()
                .results(sorted)
                .build();
    }

    // ── 1. 청년 매입임대 ────────────────────────────
    private PolicyResultDTO scoreYouthPurchase(DiagnosisRequestDTO dto, int age, boolean incomePass, boolean assetPass, Map<String, Recoentity> recoMap) {
        int score = 0;
        if (Boolean.TRUE.equals(dto.getHouseless()))  score += 30;
        if (!Boolean.TRUE.equals(dto.getMarried()))   score += 15;
        if (isYouthTarget(dto, age))                  score += 25;
        if (incomePass)                               score += 20;
        if (assetPass)                                score += 10;
        return build("청년 매입임대", score,
                "무주택 청년 조건에 해당하며, 소득·자산 기준을 충족할 가능성이 있어 청년 매입임대 추천 대상입니다.", recoMap);
    }

    // ── 2. 청년 전세임대 ────────────────────────────
    private PolicyResultDTO scoreYouthJeonse(DiagnosisRequestDTO dto, int age, boolean incomePass, boolean assetPass, Map<String, Recoentity> recoMap) {
        int score = 0;
        if (Boolean.TRUE.equals(dto.getHouseless()))  score += 30;
        if (!Boolean.TRUE.equals(dto.getMarried()))   score += 15;
        if (isYouthTarget(dto, age))                  score += 25;
        if (incomePass)                               score += 20;
        if (assetPass)                                score += 10;
        return build("청년 전세임대", score,
                "전세 형태의 주거를 희망하는 무주택 청년에게 적합한 제도입니다.", recoMap);
    }

    // ── 3. 행복주택 대학생 계층 ─────────────────────
    private PolicyResultDTO scoreHappyStudent(DiagnosisRequestDTO dto, boolean incomePass, boolean assetPass, Map<String, Recoentity> recoMap) {
        int score = 0;
        if (dto.getEmploymentStatus() == EmploymentStatus.STUDENT) score += 30;
        if (!Boolean.TRUE.equals(dto.getMarried()))                score += 15;
        if (Boolean.TRUE.equals(dto.getHouseless()))               score += 25;
        if (incomePass)                                            score += 20;
        if (assetPass)                                             score += 10;
        return build("행복주택 대학생 계층", score,
                "대학생 신분과 무주택 조건을 기준으로 행복주택 대학생 계층을 검토할 수 있습니다.", recoMap);
    }

    // ── 4. 행복주택 취업준비생 계층 ─────────────────
    private PolicyResultDTO scoreHappyJobSeeker(DiagnosisRequestDTO dto, boolean incomePass, boolean assetPass, Map<String, Recoentity> recoMap) {
        int score = 0;
        if (dto.getEmploymentStatus() == EmploymentStatus.JOB_SEEKER) score += 30;
        if (!Boolean.TRUE.equals(dto.getMarried()))                    score += 15;
        if (Boolean.TRUE.equals(dto.getHouseless()))                   score += 25;
        if (incomePass)                                                score += 20;
        if (assetPass)                                                 score += 10;
        return build("행복주택 취업준비생 계층", score,
                "취업 준비 기간 중 주거비 부담을 줄일 수 있는 제도로 검토 가능합니다.", recoMap);
    }

    // ── 5. 행복주택 청년 계층 ───────────────────────
    private PolicyResultDTO scoreHappyYouth(DiagnosisRequestDTO dto, int age, boolean incomePass, boolean assetPass, Map<String, Recoentity> recoMap) {
        int score = 0;
        if (age >= AGE_MIN && age <= AGE_MAX)          score += 30;
        if (!Boolean.TRUE.equals(dto.getMarried()))    score += 15;
        if (Boolean.TRUE.equals(dto.getHouseless()))   score += 25;
        if (incomePass)                                score += 20;
        if (assetPass)                                 score += 10;
        return build("행복주택 청년 계층", score,
                "연령, 미혼, 무주택 조건을 충족해 행복주택 청년 계층 추천 대상입니다.", recoMap);
    }

    // ── 6. 행복주택 사회초년생 계층 ─────────────────
    private PolicyResultDTO scoreHappyNewcomer(DiagnosisRequestDTO dto, boolean incomePass, boolean assetPass, Map<String, Recoentity> recoMap) {
        int score = 0;
        if (dto.getEmploymentStatus() == EmploymentStatus.NEWCOMER
                && isWithinEmploymentLimit(dto.getEmploymentPeriod())) score += 30;
        if (dto.getEmploymentStatus() == EmploymentStatus.NEWCOMER
                || dto.getEmploymentStatus() == EmploymentStatus.EMPLOYED) score += 20;
        if (!Boolean.TRUE.equals(dto.getMarried()))                    score += 15;
        if (Boolean.TRUE.equals(dto.getHouseless()))                   score += 20;
        if (incomePass && assetPass)                                   score += 15;
        return build("행복주택 사회초년생 계층", score,
                "사회초년생 조건에 해당할 가능성이 있어 행복주택 사회초년생 유형으로 검토할 수 있습니다.", recoMap);
    }

    // ── 7. 행복주택 신혼부부 계층 ───────────────────
    private PolicyResultDTO scoreHappyNewlywed(DiagnosisRequestDTO dto, boolean incomePass, boolean assetPass, Map<String, Recoentity> recoMap) {
        int score = 0;
        if (Boolean.TRUE.equals(dto.getMarried())
                && isWithinMarriageLimit(dto.getMarriagePeriod()))     score += 30;
        if (Boolean.TRUE.equals(dto.getHasYoungChild()))               score += 20;
        if (Boolean.TRUE.equals(dto.getHouseless()))                   score += 25;
        if (incomePass)                                                score += 15;
        if (assetPass)                                                 score += 10;
        return build("행복주택 신혼부부 계층", score,
                "혼인기간 또는 자녀 조건을 기준으로 신혼부부형 행복주택을 검토할 수 있습니다.", recoMap);
    }

    // ── 8. 행복주택 예비신혼부부 계층 ──────────────
    private PolicyResultDTO scoreHappyPreNewlywed(DiagnosisRequestDTO dto, boolean incomePass, boolean assetPass, Map<String, Recoentity> recoMap) {
        int score = 0;
        if (Boolean.TRUE.equals(dto.getMarriagePlan())) score += 35;
        if (Boolean.TRUE.equals(dto.getMarriagePlan()) && Boolean.TRUE.equals(dto.getHouseless()))  score += 20; // 입주 전 혼인 증명 + 무주택
        if (Boolean.TRUE.equals(dto.getHouseless()))    score += 25; // 무주택
        if (incomePass)                                 score += 10;
        if (assetPass)                                  score += 10;
        return build("행복주택 예비신혼부부 계층", score,
                "결혼 예정자로서 입주 전 혼인 증명이 가능하면 예비신혼부부 유형 검토 대상입니다.", recoMap);
    }

    // ── 9. 행복주택 한부모가족 계층 ─────────────────
    private PolicyResultDTO scoreHappySingleParent(DiagnosisRequestDTO dto, boolean incomePass, boolean assetPass, Map<String, Recoentity> recoMap) {
        int score = 0;
        if (Boolean.TRUE.equals(dto.getSingleParent()))  score += 30;
        if (Boolean.TRUE.equals(dto.getHasYoungChild())) score += 25;
        if (Boolean.TRUE.equals(dto.getHouseless()))     score += 25;
        if (incomePass)                                  score += 10;
        if (assetPass)                                   score += 10;
        return build("행복주택 한부모가족 계층", score,
                "자녀 연령과 무주택 조건을 기준으로 한부모가족 계층을 검토할 수 있습니다.", recoMap);
    }

    /** ── 공통 유틸 ─────────────────────────────────── */

    /* 청년/대학생/취업준비생 해당 여부 */
    private boolean isYouthTarget(DiagnosisRequestDTO dto, int age) {
        if (age >= AGE_MIN && age <= AGE_MAX) return true;
        return dto.getEmploymentStatus() == EmploymentStatus.STUDENT
                || dto.getEmploymentStatus() == EmploymentStatus.JOB_SEEKER;
    }

    /* 소득활동기간 5년 이내 여부 */
    private boolean isWithinEmploymentLimit(EmploymentPeriod period) {
        if (period == null) return false;
        return period == EmploymentPeriod.UNDER_1
                || period == EmploymentPeriod.YEAR_1_3
                || period == EmploymentPeriod.YEAR_3_5;
    }

    /* 혼인기간 7년 이내 여부 */
    private boolean isWithinMarriageLimit(MarriagePeriod period) {
        if (period == null) return false;
        return period == MarriagePeriod.OVER_7;
    }

    /* 추천 등급 계산 */
    private String calcGrade(int score) {
        if (score >= GRADE_ACTIVE)   return "적극추천";
        if (score >= GRADE_POSSIBLE) return "추천가능";
        if (score >= GRADE_CHECK)    return "조건부추천";
        return "추천어려움";
    }

    /**
     * PolicyResultDTO 생성 헬퍼
     * - recoentity에서 description, applyUrl 조회해서 함께 반환
     * - 매칭되는 제도 없으면 null로 반환
     */
    private PolicyResultDTO build(String name, int score, String reason, Map<String, Recoentity> recoMap) {
        Recoentity reco = recoMap.get(name);
        return PolicyResultDTO.builder()
                .policyName(name)
                .recoId(reco != null ? reco.getId() : null)
                .score(score)
                .grade(calcGrade(score))
                .reason(reason)
                .description(reco != null ? reco.getDescription() : null)
                .applyUrl(reco != null ? reco.getApplyUrl() : null)
                .build();
    }
}