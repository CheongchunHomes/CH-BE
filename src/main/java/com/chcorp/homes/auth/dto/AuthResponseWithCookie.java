package com.chcorp.homes.auth.dto;

import com.chcorp.homes.auth.dto.response.AuthTokenResponse;
import org.springframework.http.ResponseCookie;

public record AuthResponseWithCookie(
        AuthTokenResponse response,
        ResponseCookie cookie
) {
}
