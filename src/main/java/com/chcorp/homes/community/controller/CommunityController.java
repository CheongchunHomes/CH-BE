package com.chcorp.homes.community.controller;

import com.chcorp.homes.community.dto.request.CommunityCreateDTO;
import com.chcorp.homes.community.dto.request.CommunityUpdateDTO;
import com.chcorp.homes.community.dto.response.CommunityPageResponseDTO;
import com.chcorp.homes.community.dto.response.CommunityResponseDTO;
import com.chcorp.homes.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {

    private final CommunityService communityService;

    private Long resolveUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return 1L; // 로그인 기능 완성 전 임시 테스트용
        }

        return Long.valueOf(authentication.getName());
    }

    @GetMapping
    public ResponseEntity<CommunityPageResponseDTO> getPosts(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                communityService.getPosts(region, keyword, page, size)
        );
    }

    @GetMapping("/list")
    public ResponseEntity<CommunityPageResponseDTO> getPostsByListPath(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                communityService.getPosts(region, keyword, page, size)
        );
    }

    @GetMapping("/{postId}")
    public ResponseEntity<CommunityResponseDTO> getPost(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(
                communityService.getPost(postId)
        );
    }

    @PostMapping
    public ResponseEntity<CommunityResponseDTO> createPost(
            Authentication authentication,
            @RequestBody CommunityCreateDTO request
    ) {
        Long userId = resolveUserId(authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(communityService.createPost(userId, request));
    }

    @PostMapping("/add")
    public ResponseEntity<CommunityResponseDTO> createPostByAddPath(
            Authentication authentication,
            @RequestBody CommunityCreateDTO request
    ) {
        Long userId = resolveUserId(authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(communityService.createPost(userId, request));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<CommunityResponseDTO> updatePost(
            @PathVariable Long postId,
            Authentication authentication,
            @RequestBody CommunityUpdateDTO request
    ) {
        Long userId = resolveUserId(authentication);

        return ResponseEntity.ok(
                communityService.updatePost(postId, userId, request)
        );
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Long userId = resolveUserId(authentication);

        communityService.deletePost(postId, userId);

        return ResponseEntity.noContent().build();
    }
}