package com.chcorp.homes.loans.entity;

import java.util.Arrays;

public enum LoanApplicationStatus {
    PAYMENT_APPROVED("결제승인"),
    PAYMENT_PENDING("결제대기"),
    PAYMENT_REJECTED("결제거부");

    private final String dbValue;

    LoanApplicationStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static LoanApplicationStatus fromDbValue(String dbValue) {
        if (dbValue == null || dbValue.isBlank()) {
            return null;
        }

        return Arrays.stream(values())
                .filter(status -> status.dbValue.equals(dbValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown loan application status: " + dbValue));
    }
}
