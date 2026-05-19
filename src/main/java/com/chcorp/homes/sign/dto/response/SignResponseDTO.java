package com.chcorp.homes.sign.dto.response;

import com.chcorp.homes.sign.entity.SignRequest;
import com.chcorp.homes.sign.entity.SignStatus;

import java.time.Instant;

public record SignResponseDTO(
        Long signId,
        Long providerId,
        String providerNickname,
        Long customerId,
        String customerNickname,
        Long propertyId,
        String propertyTitle,
        String propertyAddress,
        SignStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static SignResponseDTO from(SignRequest signRequest) {
        return new SignResponseDTO(
                signRequest.getId(),
                signRequest.getProvider().getId(),
                signRequest.getProvider().getNickname(),
                signRequest.getCustomer().getId(),
                signRequest.getCustomer().getNickname(),
                signRequest.getPropertyId().getId(),
                signRequest.getPropertyId().getTitle(),
                signRequest.getPropertyId().getAddress(),
                signRequest.getStatus(),
                signRequest.getCreatedAt(),
                signRequest.getUpdatedAt()
        );
    }
}
