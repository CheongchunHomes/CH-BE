package com.chcorp.homes.loans.dto.response;

import com.chcorp.homes.loans.entity.LoanProduct;
import java.math.BigDecimal;
import java.time.Instant;

public record LoanProductResponseDTO(
        Long loanId,
        String externalCode,
        String provider,
        String name,
        String loanType,
        BigDecimal interestRate,
        BigDecimal interestRateMin,
        Long maxAmount,
        Long incomeLimit,
        String conditions,
        Boolean policyLoan,
        Boolean visible,
        Instant syncedAt
) {
    public static LoanProductResponseDTO from(LoanProduct product) {
        return new LoanProductResponseDTO(
                product.getLoanId(),
                product.getExternalCode(),
                product.getProvider(),
                product.getName(),
                product.getLoanType(),
                product.getInterestRate(),
                product.getInterestRateMin(),
                product.getMaxAmount(),
                product.getIncomeLimit(),
                product.getConditions(),
                product.getPolicyLoan(),
                product.getVisible(),
                product.getSyncedAt()
        );
    }
}