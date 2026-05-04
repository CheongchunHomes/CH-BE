package com.chcorp.homes.auth.dto;

import com.chcorp.homes.auth.dto.response.AccessTokenResponseDTO;
import org.springframework.http.ResponseCookie;

public record AuthResponseWithCookieDTO(
        AccessTokenResponseDTO response,
        ResponseCookie cookie
) {
}
