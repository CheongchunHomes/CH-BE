package com.chcorp.homes.auth.dto.response;

import java.time.Instant;

public record AuthLoginResponseDTO(
        String accessToken,
        Instant accessExpiresAt,
        String refreshToken,
        Instant refreshExpiresAt
) {
}
