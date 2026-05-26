package com.chcorp.homes.users.dto.request;

public record PersonalInfoRequestDTO(
        String realName,
        String phone,
        String address
) {
}
