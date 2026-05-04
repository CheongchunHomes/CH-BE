package com.chcorp.homes.auth.dto.request;

public record AuthSignupRequest(
        String email,
        String password,
        String nickname
) {
}
