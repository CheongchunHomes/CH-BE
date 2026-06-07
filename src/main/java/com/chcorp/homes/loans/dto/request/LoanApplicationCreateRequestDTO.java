package com.chcorp.homes.loans.dto.request;

public record LoanApplicationCreateRequestDTO(
        Long userId,
        Long loanId,
        Long applyAmount,
        String address
) {
}