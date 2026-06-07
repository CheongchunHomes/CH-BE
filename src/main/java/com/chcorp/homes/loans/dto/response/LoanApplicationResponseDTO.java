package com.chcorp.homes.loans.dto.response;

import com.chcorp.homes.loans.entity.LoanApplication;
import java.time.Instant;

public record LoanApplicationResponseDTO(
        Long applicationId,
        Long loanId,
        Long userId,
        Long applyAmount,
        String address,
        String status,
        Instant createdAt,
        Instant updatedAt,
        Instant decisionAt
) {
    public static LoanApplicationResponseDTO from(LoanApplication application) {
        return new LoanApplicationResponseDTO(
                application.getApplicationId(),
                application.getLoanProduct() != null ? application.getLoanProduct().getLoanId() : null,
                application.getUser() != null ? application.getUser().getId() : null,
                application.getApplyAmount(),
                application.getAddress(),
                application.getStatus() != null ? application.getStatus().name() : null,
                application.getCreatedAt(),
                application.getUpdatedAt(),
                application.getDecisionAt()
        );
    }
}