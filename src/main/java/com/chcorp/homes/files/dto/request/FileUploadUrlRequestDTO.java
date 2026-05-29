package com.chcorp.homes.files.dto.request;

import com.chcorp.homes.files.entity.FileContentType;

public record FileUploadUrlRequestDTO(
        String originalFilename,
        FileContentType contentType,
        Long sizeBytes,
        String purpose,
        Long propertyId
) {
}
