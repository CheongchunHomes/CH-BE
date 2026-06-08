package com.chcorp.homes.diagnosis.service;

import com.chcorp.homes.diagnosis.dto.request.DiagnosisRequestDTO;
import com.chcorp.homes.diagnosis.dto.response.DiagnosisResponseDTO;
import com.chcorp.homes.diagnosis.dto.response.UserProfileResponseDTO;
import com.chcorp.homes.diagnosis.entity.UserProfile;
import com.chcorp.homes.diagnosis.repository.UserProfileRepository;
import com.chcorp.homes.users.entity.User;
import com.chcorp.homes.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * DiagnosisService
 * - 프로필 진단: DB 저장 + 계산
 * - 가상 진단:  계산만 (DB 저장 없음)
 */
@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private final UserProfileRepository    userProfileRepository;
    private final UserRepository           userRepository;
    private final DiagnosisCalculator      calculator;

    /*
     * 프로필 진단
     * 1. UserProfile UPSERT (없으면 INSERT, 있으면 UPDATE)
     * 2. 계산 결과 반환
     */
    @Transactional
    public DiagnosisResponseDTO profileDiagnosis(Long userId, DiagnosisRequestDTO dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Optional<UserProfile> existing = userProfileRepository.findByUserId(userId);
        if (existing.isPresent()) {
            existing.get().updateFromRequest(dto);
        } else {
            userProfileRepository.save(buildProfile(user, dto));
        }

        return calculator.calculate(dto);
    }

    /*
     * 가상 진단
     * - 비로그인 사용자도 사용 가능 (화이트리스트 등록)
     * - DB 저장 없음, 계산 결과만 반환
     * - 결과 페이지에서 프로필 기반 점수 재계산 시에도 사용
     */
    public DiagnosisResponseDTO simulateDiagnosis(DiagnosisRequestDTO dto) {
        return calculator.calculate(dto);
    }

    // ── UserProfile 빌더 ────────────────────────────
    private UserProfile buildProfile(User user, DiagnosisRequestDTO dto) {
        return UserProfile.builder()
                .user(user)
                .birthDate(dto.getBirthDate())
                .married(dto.getMarried())
                .houseless(dto.getHouseless())
                .householdSep(dto.getHouseholdSep())
                .disabilityYn(dto.getDisabilityYn())
                .dependentCount(dto.getDependentCount())
                .currentResidence(dto.getCurrentResidence())
                .annualIncome(dto.getAnnualIncome())
                .cashAsset(dto.getCashAsset())
                .totalAsset(dto.getTotalAsset())
                .hasSubscription(dto.getHasSubscription())
                .subscriptionMonths(dto.getSubscriptionMonths())
                .desiredCity(dto.getDesiredCity())
                .desiredDistrict(dto.getDesiredDistrict())
                .desiredArea(dto.getDesiredArea())
                .desiredType(dto.getDesiredType())
                .employmentStatus(dto.getEmploymentStatus())
                .employmentPeriod(dto.getEmploymentPeriod())
                .marriagePlan(dto.getMarriagePlan())
                .marriagePeriod(dto.getMarriagePeriod())
                .hasYoungChild(dto.getHasYoungChild())
                .singleParent(dto.getSingleParent())
                .houselessYears(dto.getHouselessYears())
                .build();
    }

    // ── 프로필 조회 ──────────────────────────────
    public Optional<UserProfileResponseDTO> getMyProfile(Long userId) {
        return userProfileRepository.findByUserId(userId)
                .map(UserProfileResponseDTO::from);
    }
}