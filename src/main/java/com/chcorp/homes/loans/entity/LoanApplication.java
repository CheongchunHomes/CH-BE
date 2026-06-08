package com.chcorp.homes.loans.entity;

import com.chcorp.homes.common.entity.MutableBaseEntity;
import com.chcorp.homes.users.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Entity
@Table(name = "loan_applications")
@Getter
@ToString(exclude = {"user", "loanProduct"})
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoanApplication extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long applicationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private LoanProduct loanProduct;

    @Column(name = "apply_amount", nullable = false)
    private Long applyAmount;

    @Column(name = "address")
    private String address;

    @Setter
    @Convert(converter = LoanApplicationStatusConverter.class)
    @Column(name = "status", nullable = false, length = 20)
    private LoanApplicationStatus status;

    @Setter
    @Column(name = "decision_at")
    private Instant decisionAt;

    @PrePersist
    protected void prePersist() {
        if (status == null) {
            status = LoanApplicationStatus.PAYMENT_PENDING;
        }
    }
}
