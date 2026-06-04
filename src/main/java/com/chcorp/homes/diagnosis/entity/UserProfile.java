package com.chcorp.homes.diagnosis.entity;

import com.chcorp.homes.common.entity.MutableBaseEntity;
import com.chcorp.homes.diagnosis.dto.request.DiagnosisRequestDTO;
import com.chcorp.homes.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * 주거자격 프로필 Entity
 *
 * [역할]
 * - 사용자의 "현재 상태"를 저장하는 기준 데이터
 * - 최초 자가진단(PROFILE) 시 1회 생성
 * - 이후 수정하지 않는 것을 원칙으로 함 (불변 데이터 성격)
 *
 * [주의]
 * - 진단(Diagnosis)은 계속 쌓이는 기록
 * - 이 Entity는 "최신 상태"만 유지
 *
 * [BaseEntity]
 * - created_at / updated_at 자동 관리
 */

@Entity
@Table(name = "user_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserProfile extends MutableBaseEntity {

    /* PK */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;

    /**
     * User 1:1 관계
     * - unique = true → 유저당 하나의 프로필만 허용
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /* 생년월일 (나이 계산 및 정책 조건 판단 기준) */
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    /* 혼인 여부 (청약 조건 분기 핵심 요소) */
    @Column(name = "is_married", nullable = false)
    private Boolean married;

    /* 무주택 여부 (청약 자격 판단 핵심 조건) */
    @Column(name = "is_houseless", nullable = false)
    private Boolean houseless;

    /* 세대 분리 여부 (특정 청약 조건 적용 시 사용) */
    @Column(name = "is_household_sep", nullable = false)
    private Boolean householdSep;

    /* 장애 여부 (특별공급 등 정책 분기) */
    @Column(name = "disability_yn", nullable = false)
    private Boolean disabilityYn;

    /* 부양가족 수 (청약 가점 산정 핵심 요소) */
    @Column(name = "dependent_count", nullable = false)
    private Integer dependentCount;

    /* 현재 거주 형태 (임차 / 자가 / 기타) */
    @Column(name = "current_residence")
    private String currentResidence;

    /* 연소득 (청약 소득 기준 판단) */
    @Column(name = "annual_income")
    private Long annualIncome;

    /* 현금성 자산 (예금 등) */
    @Column(name = "cash_asset")
    private Long cashAsset;

    /* 총 자산 (부동산 포함 전체 자산) */
    @Column(name = "total_asset")
    private Long totalAsset;

    /* 청약통장 보유 여부 (필수 조건) */
    @Column(name = "has_subscription", nullable = false)
    private Boolean hasSubscription;

    /* 청약통장 가입 기간 (개월 단위, 가점 및 조건 판단) */
    @Column(name = "subscription_months")
    private Integer subscriptionMonths;

    /* 희망 도시 (청약 지역 필터링 기준) */
    @Column(name = "desired_city")
    private String desiredCity;

    /* 희망 지역구 (세부 입지 조건) */
    @Column(name = "desired_district")
    private String desiredDistrict;

    /* 희망 면적 (㎡, 예: 59, 84) */
    @Column(name = "desired_area")
    private Integer desiredArea;

    /* 희망 주택 유형 (아파트, 오피스텔 등) */
    @Column(name = "desired_type")
    private String desiredType;

    /* 고용 상태 (대학생/취업준비생/사회초년생/직장인/기타) */
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status")
    private EmploymentStatus employmentStatus;

    /* 소득활동기간 구간 */
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_period")
    private EmploymentPeriod employmentPeriod;

    /* 결혼 예정 여부 */
    @Column(name = "marriage_plan")
    private Boolean marriagePlan;

    /* 혼인기간 구간 */
    @Enumerated(EnumType.STRING)
    @Column(name = "marriage_period")
    private MarriagePeriod marriagePeriod;

    /* 만 6세 이하 자녀 여부 */
    @Column(name = "has_young_child")
    private Boolean hasYoungChild;

    /* 한부모 여부 */
    @Column(name = "is_single_parent")
    private Boolean singleParent;

    /* 무주택 기간 (년 단위, 청약가점 산정용) */
    @Column(name = "houseless_years")
    private Integer houselessYears;

    /**
     * Dirty Checking 업데이트 메서드
     * - @Transactional 안에서 호출 시 자동 UPDATE
     * - save() 재호출 불필요
     */
    public void updateFromRequest(DiagnosisRequestDTO dto) {
        this.birthDate = dto.getBirthDate();
        this.married = dto.getMarried();
        this.houseless = dto.getHouseless();
        this.householdSep = dto.getHouseholdSep();
        this.disabilityYn = dto.getDisabilityYn();
        this.dependentCount = dto.getDependentCount();
        this.currentResidence = dto.getCurrentResidence();
        this.annualIncome = dto.getAnnualIncome();
        this.cashAsset = dto.getCashAsset();
        this.totalAsset = dto.getTotalAsset();
        this.hasSubscription = dto.getHasSubscription();
        this.subscriptionMonths = dto.getSubscriptionMonths();
        this.desiredCity = dto.getDesiredCity();
        this.desiredDistrict = dto.getDesiredDistrict();
        this.desiredArea = dto.getDesiredArea();
        this.desiredType = dto.getDesiredType();
        this.employmentStatus = dto.getEmploymentStatus();
        this.employmentPeriod = dto.getEmploymentPeriod();
        this.marriagePlan = dto.getMarriagePlan();
        this.marriagePeriod = dto.getMarriagePeriod();
        this.hasYoungChild = dto.getHasYoungChild();
        this.singleParent = dto.getSingleParent();
        this.houselessYears = dto.getHouselessYears();

    }
}