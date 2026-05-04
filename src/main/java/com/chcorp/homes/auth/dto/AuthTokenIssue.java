package com.chcorp.homes.auth.dto;

import com.chcorp.homes.auth.dto.response.AuthTokenResponse;

public record AuthTokenIssue(
        AuthTokenResponse response,
        String refreshToken
) {
}
