package com.chcorp.homes.auth.dto.response;

import java.time.Instant;

public record AccessTokenResponseDTO(
        String accessToken,
        Instant accessExpiresAt
) {
}
