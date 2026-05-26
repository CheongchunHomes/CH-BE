package com.chcorp.homes.loans.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class LoanApplicationStatusConverter implements AttributeConverter<LoanApplicationStatus, String> {

    @Override
    public String convertToDatabaseColumn(LoanApplicationStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDbValue();
    }

    @Override
    public LoanApplicationStatus convertToEntityAttribute(String dbData) {
        return LoanApplicationStatus.fromDbValue(dbData);
    }
}
