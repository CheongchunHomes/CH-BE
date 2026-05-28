package com.chcorp.homes.sign.dto.response;

public record BrokerSignImageResponseDTO(
        String objectPath,
        String signedUrl,
        long expiresInSeconds,
        String contentType,
        String filename
) {
}
