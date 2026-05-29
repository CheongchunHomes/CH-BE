package com.chcorp.homes.notice.dto;

public record NoticeCreateRequestDTO(
        String category,
        String title,
        String summary,
        String content,
        Boolean important
) {
}