package com.chcorp.homes.loans.dto.response;

import com.chcorp.homes.loans.entity.LoanApplication;
import java.time.Instant;

public record LoanApplicationSummaryResponseDTO(
        Long applicationId,
        String status,
        Instant updatedAt,
        Instant createdAt,
        Long loanId,
        Long userId
) {
    public static LoanApplicationSummaryResponseDTO empty() {
        return new LoanApplicationSummaryResponseDTO(null, null, null, null, null, null);
    }

    public static LoanApplicationSummaryResponseDTO from(LoanApplication application) {
        return new LoanApplicationSummaryResponseDTO(
                application.getApplicationId(),
                application.getStatus() != null ? application.getStatus().name() : null,
                application.getUpdatedAt(),
                application.getCreatedAt(),
                application.getLoanProduct() != null ? application.getLoanProduct().getLoanId() : null,
                application.getUser() != null ? application.getUser().getId() : null
        );
    }
}