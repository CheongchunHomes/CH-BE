package com.chcorp.homes.community.dto.request;

public record CommunityCreateDTO(
        String region,
        String title,
        String content
) {
}