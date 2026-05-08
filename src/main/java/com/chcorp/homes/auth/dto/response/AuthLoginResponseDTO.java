package com.chcorp.homes.auth.dto.response;

public record AuthLoginResponseDTO(
        String accessToken,
        String refreshToken
) {
}
