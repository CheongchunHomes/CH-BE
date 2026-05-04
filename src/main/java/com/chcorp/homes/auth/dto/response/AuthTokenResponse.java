package com.chcorp.homes.auth.dto.response;

public record AuthTokenResponse(
        String accessToken,
        String tokenType,
        long expiresInMs,
        AuthUserResponse user
) {
}
