package com.chcorp.homes.files.dto.response;

public record FileUploadUrlResponseDTO(
        Long fileId,
        String objectPath,
        String signedUploadUrl,
        long expiresInSeconds
) {
}
