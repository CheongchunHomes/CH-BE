package com.chcorp.homes.auth.dto.request;

public record AuthLoginDTO(
        String email,
        String password,
        String deviceName
) {
}
