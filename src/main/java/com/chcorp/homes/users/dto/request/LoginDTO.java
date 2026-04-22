package com.chcorp.homes.users.dto.request;

import lombok.Data;

public record LoginDTO (
    String email,
    String password
){}
