package com.chcorp.homes.diagnosis.service;

import com.chcorp.homes.diagnosis.dto.request.DiagnosisRequestDTO;
import com.chcorp.homes.diagnosis.dto.response.PolicyResultDTO;
import com.chcorp.homes.diagnosis.dto.response.RecommendationResponseDTO;
import com.chcorp.homes.diagnosis.entity.EmploymentPeriod;
import com.chcorp.homes.diagnosis.entity.EmploymentStatus;
import com.chcorp.homes.diagnosis.entity.MarriagePeriod;
import com.chcorp.homes.diagnosis.entity.UserProfile;
import com.chcorp.homes.diagnosis.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * 제도 추천 채점 서비스
 * - 자가진단 결과값으로 9개 제도 100점 만점 채점
 * - 점수 내림차순 정렬 후 반환, DB 저장 없음
 * - 등급: 90↑ 적극추천 / 70↑ 추천가능 / 50↑ 조건부추천 / 50↓ 추천어려움
 * - 의존: HousingUtil (나이·소득·자산 계산)
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final HousingUtil housingUtil;
    private final UserProfileRepository userProfileRepository;

    // ── 연령 기준 상수 ──────────────────────────────
    private static final int AGE_MIN = 19;  // 청년 최소 나이
    private static final int AGE_MAX = 39;  // 청년 최대 나이

    // ── 추천 등급 기준 상수 ─────────────────────────
    private static final int GRADE_ACTIVE   = 90;  // 적극추천 기준점
    private static final int GRADE_POSSIBLE = 70;  // 추천가능 기준점
    private static final int GRADE_CHECK    = 50;  // 조건부추천 기준점

    public RecommendationResponseDTO calculate(DiagnosisRequestDTO dto) {
        int age                   = housingUtil.calcAge(dto.getBirthDate());
        boolean incomePass        = housingUtil.checkIncome(dto.getAnnualIncome(), dto.getDependentCount());
        boolean assetPassStudent  = housingUtil.checkAsset(dto.getTotalAsset(), HousingUtil.ASSET_LIMIT_STUDENT);
        boolean assetPassYouth    = housingUtil.checkAsset(dto.getTotalAsset(), HousingUtil.ASSET_LIMIT_YOUTH);
        boolean assetPassNational = housingUtil.checkAsset(dto.getTotalAsset(), HousingUtil.ASSET_LIMIT_NATIONAL);

        List<PolicyResultDTO> sorted = Stream.of(
                        scoreYouthPurchase(dto, age, incomePass, assetPassNational),
                        scoreYouthJeonse(dto, age, incomePass, assetPassNational),
                        scoreHappyStudent(dto, incomePass, assetPassStudent),
                        scoreHappyJobSeeker(dto, incomePass, assetPassYouth),
                        scoreHappyYouth(dto, age, incomePass, assetPassYouth),
                        scoreHappyNewcomer(dto, incomePass, assetPassYouth),
                        scoreHappyNewlywed(dto, incomePass, assetPassNational),
                        scoreHappyPreNewlywed(dto, incomePass, assetPassNational),
                        scoreHappySingleParent(dto, incomePass, assetPassNational)
                )
                .sorted(Comparator.comparingInt(PolicyResultDTO::getScore).reversed())
                .toList();

        return RecommendationResponseDTO.builder()
                .results(sorted)
                .build();
    }

    /**
     * 프로필 기반 추천 채점
     * - 로그인 사용자의 저장된 프로필로 채점
     * - GET /recommendation/calculate/me 에서 호출
     */
    public RecommendationResponseDTO calculateByProfile(Long userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("프로필이 없습니다."));
        return calculate(DiagnosisRequestDTO.fromProfile(profile));
    }

    // ── 1. 청년 매입임대 (국민임대 자산기준) ────────
    private PolicyResultDTO scoreYouthPurchase(DiagnosisRequestDTO dto, int age, boolean incomePass, boolean assetPass) {
        int score = 0;
        if (Boolean.TRUE.equals(dto.getHouseless()))  score += 30;
        if (!Boolean.TRUE.equals(dto.getMarried()))   score += 15;
        if (isYouthTarget(dto, age))                  score += 25;
        if (incomePass)                               score += 20;
        if (assetPass)                                score += 10;
        return build("청년 매입임대", score,
                "무주택 청년 조건에 해당하며, 소득·자산 기준을 충족할 가능성이 있어 청년 매입임대 추천 대상입니다.");
    }

    // ── 2. 청년 전세임대 (국민임대 자산기준) ────────
    private PolicyResultDTO scoreYouthJeonse(DiagnosisRequestDTO dto, int age, boolean incomePass, boolean assetPass) {
        int score = 0;
        if (Boolean.TRUE.equals(dto.getHouseless()))  score += 30;
        if (!Boolean.TRUE.equals(dto.getMarried()))   score += 15;
        if (isYouthTarget(dto, age))                  score += 25;
        if (incomePass)                               score += 20;
        if (assetPass)                                score += 10;
        return build("청년 전세임대", score,
                "전세 형태의 주거를 희망하는 무주택 청년에게 적합한 제도입니다.");
    }

    // ── 3. 행복주택 대학생 계층 (대학생 자산기준) ───
    private PolicyResultDTO scoreHappyStudent(DiagnosisRequestDTO dto, boolean incomePass, boolean assetPass) {
        int score = 0;
        if (dto.getEmploymentStatus() == EmploymentStatus.STUDENT) score += 30;
        if (!Boolean.TRUE.equals(dto.getMarried()))                score += 15;
        if (Boolean.TRUE.equals(dto.getHouseless()))               score += 25;
        if (incomePass)                                            score += 20;
        if (assetPass)                                             score += 10;
        return build("행복주택 대학생 계층", score,
                "대학생 신분과 무주택 조건을 기준으로 행복주택 대학생 계층을 검토할 수 있습니다.");
    }

    // ── 4. 행복주택 취업준비생 계층 (청년 자산기준) ─
    private PolicyResultDTO scoreHappyJobSeeker(DiagnosisRequestDTO dto, boolean incomePass, boolean assetPass) {
        int score = 0;
        if (dto.getEmploymentStatus() == EmploymentStatus.JOB_SEEKER) score += 30;
        if (!Boolean.TRUE.equals(dto.getMarried()))                    score += 15;
        if (Boolean.TRUE.equals(dto.getHouseless()))                   score += 25;
        if (incomePass)                                                score += 20;
        if (assetPass)                                                 score += 10;
        return build("행복주택 취업준비생 계층", score,
                "취업 준비 기간 중 주거비 부담을 줄일 수 있는 제도로 검토 가능합니다.");
    }

    // ── 5. 행복주택 청년 계층 (청년 자산기준) ───────
    private PolicyResultDTO scoreHappyYouth(DiagnosisRequestDTO dto, int age, boolean incomePass, boolean assetPass) {
        int score = 0;
        if (age >= AGE_MIN && age <= AGE_MAX)          score += 30;
        if (!Boolean.TRUE.equals(dto.getMarried()))    score += 15;
        if (Boolean.TRUE.equals(dto.getHouseless()))   score += 25;
        if (incomePass)                                score += 20;
        if (assetPass)                                 score += 10;
        return build("행복주택 청년 계층", score,
                "연령, 미혼, 무주택 조건을 충족해 행복주택 청년 계층 추천 대상입니다.");
    }

    // ── 6. 행복주택 사회초년생 계층 (청년 자산기준) ─
    private PolicyResultDTO scoreHappyNewcomer(DiagnosisRequestDTO dto, boolean incomePass, boolean assetPass) {
        int score = 0;
        if (dto.getEmploymentStatus() == EmploymentStatus.NEWCOMER
                && isWithinEmploymentLimit(dto.getEmploymentPeriod())) score += 30;
        if (dto.getEmploymentStatus() == EmploymentStatus.NEWCOMER
                || dto.getEmploymentStatus() == EmploymentStatus.EMPLOYED) score += 20;
        if (!Boolean.TRUE.equals(dto.getMarried()))                    score += 15;
        if (Boolean.TRUE.equals(dto.getHouseless()))                   score += 20;
        if (incomePass && assetPass)                                   score += 15;
        return build("행복주택 사회초년생 계층", score,
                "사회초년생 조건에 해당할 가능성이 있어 행복주택 사회초년생 유형으로 검토할 수 있습니다.");
    }

    // ── 7. 행복주택 신혼부부 계층 (국민임대 자산기준)
    private PolicyResultDTO scoreHappyNewlywed(DiagnosisRequestDTO dto, boolean incomePass, boolean assetPass) {
        int score = 0;
        if (Boolean.TRUE.equals(dto.getMarried())
                && isWithinMarriageLimit(dto.getMarriagePeriod()))     score += 30;
        if (Boolean.TRUE.equals(dto.getHasYoungChild()))               score += 20;
        if (Boolean.TRUE.equals(dto.getHouseless()))                   score += 25;
        if (incomePass)                                                score += 15;
        if (assetPass)                                                 score += 10;
        return build("행복주택 신혼부부 계층", score,
                "혼인기간 또는 자녀 조건을 기준으로 신혼부부형 행복주택을 검토할 수 있습니다.");
    }

    // ── 8. 행복주택 예비신혼부부 계층 (국민임대 자산기준)
    private PolicyResultDTO scoreHappyPreNewlywed(DiagnosisRequestDTO dto, boolean incomePass, boolean assetPass) {
        int score = 0;
        if (Boolean.TRUE.equals(dto.getMarriagePlan()))                score += 35;
        if (Boolean.TRUE.equals(dto.getMarriagePlan())
                && Boolean.TRUE.equals(dto.getHouseless()))            score += 20;
        if (Boolean.TRUE.equals(dto.getHouseless()))                   score += 25;
        if (incomePass)                                                score += 10;
        if (assetPass)                                                 score += 10;
        return build("행복주택 예비신혼부부 계층", score,
                "결혼 예정자로서 입주 전 혼인 증명이 가능하면 예비신혼부부 유형 검토 대상입니다.");
    }

    // ── 9. 행복주택 한부모가족 계층 (국민임대 자산기준)
    private PolicyResultDTO scoreHappySingleParent(DiagnosisRequestDTO dto, boolean incomePass, boolean assetPass) {
        int score = 0;
        if (Boolean.TRUE.equals(dto.getSingleParent()))  score += 30;
        if (Boolean.TRUE.equals(dto.getHasYoungChild())) score += 25;
        if (Boolean.TRUE.equals(dto.getHouseless()))     score += 25;
        if (incomePass)                                  score += 10;
        if (assetPass)                                   score += 10;
        return build("행복주택 한부모가족 계층", score,
                "자녀 연령과 무주택 조건을 기준으로 한부모가족 계층을 검토할 수 있습니다.");
    }

    // ── 공통 유틸 ───────────────────────────────────

    private boolean isYouthTarget(DiagnosisRequestDTO dto, int age) {
        // 만 19~39세면 무조건 통과
        if (age >= AGE_MIN && age <= AGE_MAX) return true;
        // 대학생/취준생은 만 17세 이상이면 예외 허용
        if (age >= 17) {
            return dto.getEmploymentStatus() == EmploymentStatus.STUDENT
                    || dto.getEmploymentStatus() == EmploymentStatus.JOB_SEEKER;
        }
        return false;
    }

    private boolean isWithinEmploymentLimit(EmploymentPeriod period) {
        if (period == null) return false;
        return period == EmploymentPeriod.UNDER_1
                || period == EmploymentPeriod.YEAR_1_3
                || period == EmploymentPeriod.YEAR_3_5;
    }

    private boolean isWithinMarriageLimit(MarriagePeriod period) {
        if (period == null) return false;
        return period == MarriagePeriod.WITHIN_7;
    }

    private String calcGrade(int score) {
        if (score >= GRADE_ACTIVE)   return "적극추천";
        if (score >= GRADE_POSSIBLE) return "추천가능";
        if (score >= GRADE_CHECK)    return "조건부추천";
        return "추천어려움";
    }

    /* PolicyResultDTO 생성 헬퍼 - description/applyUrl은 추천 파트 Announcement 연동 후 추가 예정 */
    private PolicyResultDTO build(String name, int score, String reason) {
        return PolicyResultDTO.builder()
                .policyName(name)
                .recoId(null)
                .score(score)
                .grade(calcGrade(score))
                .reason(reason)
                .description(null)
                .applyUrl(null)
                .build();
    }
}