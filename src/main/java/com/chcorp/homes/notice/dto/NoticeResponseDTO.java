package com.chcorp.homes.notice.dto;

import com.chcorp.homes.notice.entity.Notice;

import java.time.Instant;

public record NoticeResponseDTO(
        Long noticeId,
        String category,
        String title,
        String summary,
        String content,
        boolean important,
        int viewCount,
        Instant createdAt
) {
    public static NoticeResponseDTO from(Notice notice) {
        return new NoticeResponseDTO(
                notice.getNoticeId(),
                notice.getCategory(),
                notice.getTitle(),
                notice.getSummary(),
                notice.getContent(),
                notice.isImportant(),
                notice.getViewCount(),
                notice.getCreatedAt()
        );
    }
}