package com.chcorp.homes.diagnosis.dto.response;

import com.chcorp.homes.diagnosis.entity.EmploymentPeriod;
import com.chcorp.homes.diagnosis.entity.EmploymentStatus;
import com.chcorp.homes.diagnosis.entity.MarriagePeriod;
import com.chcorp.homes.diagnosis.entity.UserProfile;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 마이페이지 프로필 조회 응답 DTO
 * - GET /diagnosis/me 응답용
 */
@Getter
@Builder
public class UserProfileResponseDTO {

    private LocalDate birthDate;
    private Boolean married;
    private Boolean houseless;
    private Boolean householdSep;
    private Boolean disabilityYn;
    private Integer dependentCount;
    private String currentResidence;
    private Long annualIncome;
    private Long totalAsset;
    private Long cashAsset;
    private Boolean hasSubscription;
    private Integer subscriptionMonths;
    private String desiredCity;
    private String desiredDistrict;
    private Integer desiredArea;
    private String desiredType;
    private EmploymentStatus employmentStatus;
    private EmploymentPeriod employmentPeriod;
    private Boolean marriagePlan;
    private MarriagePeriod marriagePeriod;
    private Boolean hasYoungChild;
    private Boolean singleParent;
    private Integer houselessYears;

    /* UserProfile 엔티티 → DTO 변환 */
    public static UserProfileResponseDTO from(UserProfile profile) {
        return UserProfileResponseDTO.builder()
                .birthDate(profile.getBirthDate())
                .married(profile.getMarried())
                .houseless(profile.getHouseless())
                .householdSep(profile.getHouseholdSep())
                .disabilityYn(profile.getDisabilityYn())
                .dependentCount(profile.getDependentCount())
                .currentResidence(profile.getCurrentResidence())
                .annualIncome(profile.getAnnualIncome())
                .totalAsset(profile.getTotalAsset())
                .cashAsset(profile.getCashAsset())
                .hasSubscription(profile.getHasSubscription())
                .subscriptionMonths(profile.getSubscriptionMonths())
                .desiredCity(profile.getDesiredCity())
                .desiredDistrict(profile.getDesiredDistrict())
                .desiredArea(profile.getDesiredArea())
                .desiredType(profile.getDesiredType())
                .employmentStatus(profile.getEmploymentStatus())
                .employmentPeriod(profile.getEmploymentPeriod())
                .marriagePlan(profile.getMarriagePlan())
                .marriagePeriod(profile.getMarriagePeriod())
                .hasYoungChild(profile.getHasYoungChild())
                .singleParent(profile.getSingleParent())
                .houselessYears(profile.getHouselessYears())
                .build();
    }
}