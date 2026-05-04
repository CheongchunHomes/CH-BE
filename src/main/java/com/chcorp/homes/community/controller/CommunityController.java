package com.chcorp.homes.community.controller;

import com.chcorp.homes.community.entity.CommunityPost;
import com.chcorp.homes.community.repository.CommunityPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityPostRepository communityPostRepository;

    @PostMapping("/add")
    public CommunityPost addPost(@RequestBody CommunityPost post) {
        if (post.getUserId() == null) {
            post.setUserId(1L);
        }

        if (post.getViewCount() == null) {
            post.setViewCount(0L);
        }

        if (post.getCreatedAt() == null) {
            post.setCreatedAt(LocalDateTime.now());
        }

        return communityPostRepository.save(post);
    }

    @GetMapping("/list")
    public List<CommunityPost> getPosts(@RequestParam(required = false) String region) {
        if (region != null && !region.isBlank()) {
            return communityPostRepository.findByRegionOrderByCreatedAtDesc(region);
        }

        return communityPostRepository.findAllByOrderByCreatedAtDesc();
    }

    @GetMapping("/{postId}")
    public CommunityPost getPost(@PathVariable Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        post.setViewCount(post.getViewCount() + 1);
        return communityPostRepository.save(post);
    }
}