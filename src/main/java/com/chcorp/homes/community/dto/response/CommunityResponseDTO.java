package com.chcorp.homes.community.dto.response;

import com.chcorp.homes.community.entity.CommunityPost;

import java.time.Instant;

public record CommunityResponseDTO(
        Long postId,
        Long userId,
        String region,
        String title,
        String content,
        Integer viewCount,
        Instant createdAt
) {
    public static CommunityResponseDTO from(CommunityPost post) {
        return new CommunityResponseDTO(
                post.getPostId(),
                post.getUserId(),
                post.getRegion(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getCreatedAt()
        );
    }
}