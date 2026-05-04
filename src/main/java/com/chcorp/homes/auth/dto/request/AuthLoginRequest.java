package com.chcorp.homes.auth.dto.request;

public record AuthLoginRequest(
        String email,
        String password,
        String deviceName
) {
}
