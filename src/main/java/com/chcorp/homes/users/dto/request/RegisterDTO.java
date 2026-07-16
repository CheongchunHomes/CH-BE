package com.chcorp.homes.users.dto.request;

public record RegisterDTO(
        String email,
        String password,
        String nickname
) {
}
