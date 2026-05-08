package com.chcorp.homes.auth.dto.request;

public record AuthReauthRequestDTO(
        String refreshToken,
        String password
) {
}
