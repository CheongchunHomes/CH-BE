package com.chcorp.homes.auth.dto.response;

import java.time.Instant;

public record ReauthResponseDTO(
        String accessToken,
        Instant refreshExpiresAt
) {
}
