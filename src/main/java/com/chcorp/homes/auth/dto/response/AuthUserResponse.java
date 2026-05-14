package com.chcorp.homes.auth.dto.response;

import com.chcorp.homes.users.entity.User;

public record AuthUserResponse(
        Long id,
        String email,
        String nickname,
        String role
) {
    public static AuthUserResponse from(User user) {
        return new AuthUserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole().name()
        );
    }
}
