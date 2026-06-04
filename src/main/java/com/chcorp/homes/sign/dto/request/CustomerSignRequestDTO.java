package com.chcorp.homes.sign.dto.request;

public record CustomerSignRequestDTO(
        Long customerSignatureFileId,
        Long completedPdfFileId
) {
}
