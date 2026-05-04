package com.chcorp.homes.diagnosis.entity;

import com.chcorp.homes.common.entity.MutableBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_diagnosis_results")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DiagnosisResult extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diagnosisResultId;

    private Long userId;
    private String homelessStatus;
    private String ageStatus;
    private String incomeStatus;
    private String assetStatus;
    private String familyStatus;
    private String subscriptionStatus;
    private Integer subscriptionReadinessScore;
    private Integer publicRentalFitScore;
    @Column(name = "jeonse_loan_score")
    private Integer jeonseloanScore;
    private Integer saleSubscriptionScore;
    private String subscriptionReadinessGrade;
    private String publicRentalFitGrade;
    @Column(name = "jeonse_loan_grade")
    private String jeonseloanGrade;
    private String saleSubscriptionGrade;
    private LocalDate diagnosisBaseDate;

}
