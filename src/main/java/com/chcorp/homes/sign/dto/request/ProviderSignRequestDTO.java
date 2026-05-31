package com.chcorp.homes.sign.dto.request;

import java.time.LocalDate;

public record ProviderSignRequestDTO(
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
        Long providerSignatureFileId
) {
}
