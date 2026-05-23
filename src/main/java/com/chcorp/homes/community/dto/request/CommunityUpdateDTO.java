package com.chcorp.homes.community.dto.request;

public record CommunityUpdateDTO(
        String region,
        String title,
        String content
) {
}