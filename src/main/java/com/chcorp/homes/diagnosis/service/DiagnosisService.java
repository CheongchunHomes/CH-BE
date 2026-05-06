package com.chcorp.homes.diagnosis.service;

import com.chcorp.homes.diagnosis.dto.request.DiagnosisRequestDTO;
import com.chcorp.homes.diagnosis.dto.response.DiagnosisResponseDTO;
import com.chcorp.homes.diagnosis.entity.Diagnosis;
import com.chcorp.homes.diagnosis.entity.UserProfile;
import com.chcorp.homes.diagnosis.repository.DiagnosisRepository;
import com.chcorp.homes.diagnosis.repository.UserProfileRepository;
import com.chcorp.homes.users.entity.User;
import com.chcorp.homes.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * ─────────────────────────────────────────────
 * DiagnosisService
 * - 프로필 진단: DB 저장 + 계산
 * - 가상 진단:  계산만 (DB 저장 없음)
 * ─────────────────────────────────────────────
 */
@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private final DiagnosisRepository      diagnosisRepository;
    private final UserProfileRepository    userProfileRepository;
    private final UserRepository           userRepository;
    private final DiagnosisCalculator      calculator;

    /*
     * 프로필 진단
     * 1. UserProfile UPSERT (없으면 INSERT, 있으면 UPDATE)
     * 2. Diagnosis INSERT (히스토리)
     * 3. 계산 결과 반환
     */
    @Transactional
    public DiagnosisResponseDTO profileDiagnosis(Long userId, DiagnosisRequestDTO dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // UserProfile UPSERT
        Optional<UserProfile> existing = userProfileRepository.findByUser_Id(userId);
        if (existing.isPresent()) {
            // 이미 있으면 UPDATE (Dirty Checking 활용)
            existing.get().updateFromRequest(dto);
        } else {
            // 첫 진단이면 INSERT
            userProfileRepository.save(buildProfile(user, dto));
        }

        // Diagnosis 히스토리 저장
        diagnosisRepository.save(buildDiagnosis(user, dto));

        // 계산 결과 반환
        return calculator.calculate(dto);
    }

    /*
     * 가상 진단
     * - DB 저장 없음
     * - 계산만 해서 바로 반환
     */
    public DiagnosisResponseDTO simulateDiagnosis(DiagnosisRequestDTO dto) {
        return calculator.calculate(dto);
    }

    // ── UserProfile 빌더 ────────────────────────────
    // UserProfile 엔티티 필드명 기준으로 맞춤
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
                .build();
    }

    // ── Diagnosis 빌더 ──────────────────────────────
    // Diagnosis 엔티티 birthDate가 String 타입이므로 toString() 변환
    private Diagnosis buildDiagnosis(User user, DiagnosisRequestDTO dto) {
        return Diagnosis.builder()
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
                .build();
        }
    }

