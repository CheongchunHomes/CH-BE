package com.chcorp.homes.users.dto.response;

import java.time.Instant;

public record MyProfileDTO(
        String email,
        String nickname,
        Instant createdAt
) {
}
