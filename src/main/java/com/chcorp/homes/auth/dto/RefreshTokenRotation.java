package com.chcorp.homes.auth.dto;

import com.chcorp.homes.users.entity.User;
import org.springframework.http.ResponseCookie;

public record RefreshTokenRotation(
        User user,
        ResponseCookie refreshCookie
) {
}
