package com.chcorp.homes.community.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record CommunityPageResponseDTO(
        List<CommunityResponseDTO> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    public static CommunityPageResponseDTO from(Page<CommunityResponseDTO> pageResult) {
        return new CommunityPageResponseDTO(
                pageResult.getContent(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.isFirst(),
                pageResult.isLast()
        );
    }
}