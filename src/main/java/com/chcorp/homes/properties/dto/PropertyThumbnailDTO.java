package com.chcorp.homes.properties.dto;

public record PropertyThumbnailDTO(
        String thumbnailUrl,
        Long expiresInSeconds
) {
}
