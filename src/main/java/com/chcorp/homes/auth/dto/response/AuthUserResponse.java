package com.chcorp.homes.auth.dto.response;

import com.chcorp.homes.users.entity.User;

public record AuthUserResponse(
        Long id,
        String email,
        String nickname,
        String role,
        boolean hasPersonalInfo,
        String authLevel
) {
    public static AuthUserResponse from(User user, boolean hasPersonalInfo, String authLevel) {
        return new AuthUserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole().name(),
                hasPersonalInfo,
                authLevel
        );
    }
}
