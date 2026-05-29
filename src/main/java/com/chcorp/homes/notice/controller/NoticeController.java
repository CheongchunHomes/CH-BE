package com.chcorp.homes.notice.controller;

import com.chcorp.homes.notice.dto.NoticeResponseDTO;
import com.chcorp.homes.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public ResponseEntity<List<NoticeResponseDTO>> getNotices() {
        return ResponseEntity.ok(noticeService.getNotices());
    }

    @GetMapping("/community/latest")
    public ResponseEntity<List<NoticeResponseDTO>> getLatestCommunityNotices() {
        return ResponseEntity.ok(noticeService.getLatestCommunityNotices());
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponseDTO> getNotice(@PathVariable Long noticeId) {
        return ResponseEntity.ok(noticeService.getNotice(noticeId));
    }
}
