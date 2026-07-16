package com.chcorp.homes.diagnosis.entity;

import com.chcorp.homes.common.entity.BaseEntity;
import com.chcorp.homes.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * ─────────────────────────────────────────────
 * 자가진단 결과 저장 Entity
 * - FE에서 입력한 모든 데이터를 DB에 저장
 * - 이후 분석 로직 및 통계 활용 가능
 * ─────────────────────────────────────────────
 */
@Entity
@Table(name = "diagnosis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Diagnosis extends BaseEntity {

    /* PK (자동 증가) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /* 생년월일 (yyyy-MM-dd 문자열) */
    @Column(nullable = false)
    private LocalDate birthDate;

    /* 혼인 여부 */
    @Column(nullable = false)
    private Boolean married;

    /* 무주택 여부 */
    @Column(nullable = false)
    private Boolean houseless;

    /* 세대 분리 여부 */
    @Column(nullable = false)
    private Boolean householdSep;

    /* 장애 여부 */
    @Column(nullable = false)
    private Boolean disabilityYn;

    /* 부양가족 수 */
    @Column(nullable = false)
    private Integer dependentCount;

    /* 현재 거주 형태 (임차 / 자가 / 기타) */
    @Column(nullable = false)
    private String currentResidence;

    /* 연소득 (원 단위) */
    @Column(nullable = false)
    private Long annualIncome;

    /* 총 자산 (원 단위) */
    @Column(nullable = false)
    private Long totalAsset;

    /* 현금성 자산 (원 단위) */
    @Column(nullable = false)
    private Long cashAsset;

    /* 청약통장 보유 여부 */
    @Column(nullable = false)
    private Boolean hasSubscription;

    /* 청약 가입 개월 수 */
    @Column(nullable = false)
    private Integer subscriptionMonths;

    /* 희망 도시 */
    @Column(nullable = false)
    private String desiredCity;

    /* 희망 지역구 */
    @Column(nullable = false)
    private String desiredDistrict;

    /* 희망 면적 (㎡) */
    @Column(nullable = false)
    private Integer desiredArea;

    /* 희망 주택 유형 */
    @Column(nullable = false)
    private String desiredType;

    /* 고용 상태 */
    @Enumerated(EnumType.STRING)
    @Column
    private EmploymentStatus employmentStatus;

    /* 소득활동기간 구간 */
    @Enumerated(EnumType.STRING)
    @Column
    private EmploymentPeriod employmentPeriod;

    /* 결혼 예정 여부 */
    @Column
    private Boolean marriagePlan;

    /* 혼인기간 구간 */
    @Enumerated(EnumType.STRING)
    @Column
    private MarriagePeriod marriagePeriod;

    /* 만 6세 이하 자녀 여부 */
    @Column
    private Boolean hasYoungChild;

    /* 한부모 여부 */
    @Column
    private Boolean singleParent;
}