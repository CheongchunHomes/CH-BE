package com.chcorp.homes.files.dto.response;

import com.chcorp.homes.files.entity.FileContentType;

public record FileSignedUrlResponseDTO(
        Long fileId,
        String signedUrl,
        long expiresInSeconds,
        FileContentType contentType,
        String originalFilename,
        Long sizeBytes
) {
}
