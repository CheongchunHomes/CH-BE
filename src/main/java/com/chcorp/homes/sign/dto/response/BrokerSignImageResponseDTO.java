package com.chcorp.homes.sign.dto.response;

public record BrokerSignImageResponseDTO(
        String signedUrl,
        long expiresInSeconds
) {
}
