package com.chcorp.homes.diagnosis.dto.request;

import com.chcorp.homes.diagnosis.entity.EmploymentPeriod;
import com.chcorp.homes.diagnosis.entity.EmploymentStatus;
import com.chcorp.homes.diagnosis.entity.MarriagePeriod;
import lombok.Getter;
import java.time.LocalDate;

/**
 * ─────────────────────────────────────────────
 * 자가진단 요청 DTO (공통)
 * - 프로필 진단 / 가상 진단 둘 다 사용
 * - isProfileDiagnosis: true → DB 저장, false → 계산만
 * ─────────────────────────────────────────────
 */
@Getter
public class DiagnosisRequestDTO {

    /* 프로필 저장 여부 (true: 실제진단, false: 가상진단) */
    private Boolean isProfileDiagnosis;

    /* 생년월일 */
    private LocalDate birthDate;

    /* 혼인 여부 */
    private Boolean married;

    /* 무주택 여부 */
    private Boolean houseless;

    /* 세대 분리 여부 */
    private Boolean householdSep;

    /* 장애 여부 */
    private Boolean disabilityYn;

    /* 부양가족 수 */
    private Integer dependentCount;

    /* 현재 거주 형태 */
    private String currentResidence;

    /* 연소득 (원 단위) */
    private Long annualIncome;

    /* 총 자산 (원 단위) */
    private Long totalAsset;

    /* 현금성 자산 (원 단위) */
    private Long cashAsset;

    /* 청약통장 보유 여부 */
    private Boolean hasSubscription;

    /* 청약 가입 개월 수 */
    private Integer subscriptionMonths;

    /* 희망 도시 */
    private String desiredCity;

    /* 희망 지역구 */
    private String desiredDistrict;

    /* 희망 면적 (㎡) */
    private Integer desiredArea;

    /* 희망 주택 유형 */
    private String desiredType;

    /* 고용 상태 */
    private EmploymentStatus employmentStatus;

    /* 소득활동기간 구간 */
    private EmploymentPeriod employmentPeriod;

    /* 결혼 예정 여부 */
    private Boolean marriagePlan;

    /* 혼인기간 구간 */
    private MarriagePeriod marriagePeriod;

    /* 만 6세 이하 자녀 여부 */
    private Boolean hasYoungChild;

    /* 한부모 여부 */
    private Boolean singleParent;

}