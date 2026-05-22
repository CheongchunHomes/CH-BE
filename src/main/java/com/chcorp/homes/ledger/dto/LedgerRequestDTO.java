package com.chcorp.homes.ledger.dto;

import java.time.LocalDate;

public record LedgerRequestDTO(
        String category,
        Long amount,
        String method,
        String memo,
        LocalDate spentAt
) {
}