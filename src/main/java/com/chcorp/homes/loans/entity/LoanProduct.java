package com.chcorp.homes.loans.entity;

import com.chcorp.homes.common.entity.MutableBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "loan_products",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_loan_products_provider_external_code",
                        columnNames = {"provider", "external_code"}
                )
        }
)
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoanProduct extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_id")
    private Long loanId;

    @Column(name = "external_code", nullable = false)
    private String externalCode;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String name;

    @Column(name = "loan_type", nullable = false)
    private String loanType;

    @Column(name = "interest_rate", precision = 6, scale = 3)
    private BigDecimal interestRate;

    @Column(name = "interest_rate_min", precision = 6, scale = 3)
    private BigDecimal interestRateMin;

    @Column(name = "max_amount")
    private Long maxAmount;

    @Column(name = "income_limit")
    private Long incomeLimit;

    @Column(columnDefinition = "TEXT")
    private String conditions;

    @Column(name = "is_policy_loan", nullable = false)
    private Boolean policyLoan;

    @Column(name = "is_visible", nullable = false)
    private Boolean visible;

    @Column(name = "synced_at")
    private Instant syncedAt;
}
