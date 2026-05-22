package com.chcorp.homes.announcements.controller;

import com.chcorp.homes.announcements.dto.AnnouncementScrapDTO;
import com.chcorp.homes.announcements.service.AnnouncementScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/announcements-scraps")
public class AnnouncementScrapController {

    private final AnnouncementScrapService announcementScrapService;

    // 공고 스크랩 등록
    // 로그인한 사용자가 특정 공고를 스크랩할 때 사용
    @PostMapping("/{announcementId}")
    public ResponseEntity<Void> addScrap(
            @PathVariable Long announcementId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        announcementScrapService.addScrap(userId, announcementId);
        return ResponseEntity.ok().build();
    }

    // 공고 스크랩 취소
    // 로그인한 사용자가 이미 스크랩한 공고를 다시 해제할 때 사용
    @DeleteMapping("/{announcementId}")
    public ResponseEntity<Void> removeScrap(
            @PathVariable Long announcementId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        announcementScrapService.removeScrap(userId, announcementId);
        return ResponseEntity.ok().build();
    }

    // 내 스크랩 공고 목록 조회
    // 마이페이지에서 스크랩한 공고 리스트를 보여줄 때 사용
    @GetMapping("/me")
    public ResponseEntity<List<AnnouncementScrapDTO>> getMyScraps(
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(announcementScrapService.getMyScraps(userId));
    }

    // 내가 스크랩한 공고 ID 목록 조회
    // 공고 리스트에서 하트 버튼 상태를 표시할 때 사용
    @GetMapping("/me/ids")
    public ResponseEntity<List<Long>> getMyScrapAnnouncementIds(
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(announcementScrapService.getMyScrapAnnouncementIds(userId));
    }

    // JWT 인증 정보에서 로그인한 사용자 ID 추출
    // JwtTokenProvider에서 principal에 userId를 문자열로 넣어두었기 때문에 getName()으로 가져옴
    private Long getUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        return Long.valueOf(authentication.getName());
    }
}
