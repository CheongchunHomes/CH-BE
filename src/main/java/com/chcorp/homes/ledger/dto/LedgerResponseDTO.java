package com.chcorp.homes.ledger.dto;

import com.chcorp.homes.ledger.entity.Ledger;

import java.time.LocalDate;

public record LedgerResponseDTO(
        Long expenditureId,
        Long userId,
        String category,
        Long amount,
        String method,
        String memo,
        LocalDate spentAt
) {
    public static LedgerResponseDTO from(Ledger ledger) {
        return new LedgerResponseDTO(
                ledger.getExpenditureId(),
                ledger.getUserId(),
                ledger.getCategory(),
                ledger.getAmount(),
                ledger.getMethod(),
                ledger.getMemo(),
                ledger.getSpentAt()
        );
    }
}