package com.chcorp.homes.sign.dto.response;

import com.chcorp.homes.sign.entity.SignRequest;
import com.chcorp.homes.sign.entity.SignStatus;

import java.time.Instant;

public record SignResponseDTO(
        Long signId,
        Long providerId,
        Long customerId,
        Long propertyId,
        SignStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static SignResponseDTO from(SignRequest signRequest) {
        return new SignResponseDTO(
                signRequest.getId(),
                signRequest.getProvider().getId(),
                signRequest.getCustomer().getId(),
                signRequest.getPropertyId().getId(),
                signRequest.getStatus(),
                signRequest.getCreatedAt(),
                signRequest.getUpdatedAt()
        );
    }
}
