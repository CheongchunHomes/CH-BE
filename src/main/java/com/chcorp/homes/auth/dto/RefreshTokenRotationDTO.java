package com.chcorp.homes.auth.dto;

import com.chcorp.homes.users.entity.User;
import org.springframework.http.ResponseCookie;

public record RefreshTokenRotationDTO(
        User user,
        ResponseCookie refreshCookie
) {
}
