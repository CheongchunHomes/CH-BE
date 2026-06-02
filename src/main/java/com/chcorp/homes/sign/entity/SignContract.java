package com.chcorp.homes.sign.entity;

import com.chcorp.homes.common.entity.MutableBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "sign_contract")
public class SignContract extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sign_request_id", nullable = false, unique = true)
    private SignRequest signRequest;

    @Column(name = "lease_end_date")
    private LocalDate leaseEndDate;

    @Column(name = "contract_amount")
    private Long contractAmount;

    @Column(name = "interim_amount1")
    private Long interimAmount1;

    @Column(name = "interim_amount1_date")
    private LocalDate interimAmount1Date;

    @Column(name = "interim_amount2")
    private Long interimAmount2;

    @Column(name = "interim_amount2_date")
    private LocalDate interimAmount2Date;

    @Column(name = "balance_amount")
    private Long balanceAmount;

    @Column(name = "balance_date")
    private LocalDate balanceDate;

    @Column(name = "special_terms", columnDefinition = "TEXT")
    private String specialTerms;

    @Column(name = "building_dong")
    private String buildingDong;

    @Column(name = "unit_ho")
    private String unitHo;

    @Column(name = "rented_part")
    private String rentedPart;

    @Column(name = "provider_signature_file_id")
    private Long providerSignatureFileId;

    @Column(name = "customer_signature_file_id")
    private Long customerSignatureFileId;

    @Column(name = "completed_pdf_file_id")
    private Long completedPdfFileId;

    @Column(name = "provider_signed_at")
    private Instant providerSignedAt;

    @Column(name = "customer_signed_at")
    private Instant customerSignedAt;

    public void saveProviderContract(
            LocalDate leaseEndDate,
            Long contractAmount,
            Long interimAmount1,
            LocalDate interimAmount1Date,
            Long interimAmount2,
            LocalDate interimAmount2Date,
            Long balanceAmount,
            LocalDate balanceDate,
            String specialTerms,
            String buildingDong,
            String unitHo,
            String rentedPart,
            Long providerSignatureFileId,
            Instant providerSignedAt
    ) {
        this.leaseEndDate = leaseEndDate;
        this.contractAmount = contractAmount;
        this.interimAmount1 = interimAmount1;
        this.interimAmount1Date = interimAmount1Date;
        this.interimAmount2 = interimAmount2;
        this.interimAmount2Date = interimAmount2Date;
        this.balanceAmount = balanceAmount;
        this.balanceDate = balanceDate;
        this.specialTerms = specialTerms;
        this.buildingDong = buildingDong;
        this.unitHo = unitHo;
        this.rentedPart = rentedPart;
        this.providerSignatureFileId = providerSignatureFileId;
        this.providerSignedAt = providerSignedAt;
    }

    public void saveCustomerSignature(
            Long customerSignatureFileId,
            Long completedPdfFileId,
            Instant customerSignedAt
    ) {
        this.customerSignatureFileId = customerSignatureFileId;
        this.completedPdfFileId = completedPdfFileId;
        this.customerSignedAt = customerSignedAt;
    }
}
