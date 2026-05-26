package com.chcorp.homes.community.service;

import com.chcorp.homes.community.dto.request.CommunityCreateDTO;
import com.chcorp.homes.community.dto.request.CommunityUpdateDTO;
import com.chcorp.homes.community.dto.response.CommunityPageResponseDTO;
import com.chcorp.homes.community.dto.response.CommunityResponseDTO;
import com.chcorp.homes.community.entity.CommunityPost;
import com.chcorp.homes.community.repository.CommunityPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityService {

    private final CommunityPostRepository communityPostRepository;

    @Transactional
    public CommunityResponseDTO createPost(Long userId, CommunityCreateDTO request) {
        CommunityPost post = CommunityPost.builder()
                .userId(userId)
                .region(request.region())
                .title(request.title())
                .content(request.content())
                .viewCount(0)
                .build();

        CommunityPost savedPost = communityPostRepository.save(post);

        return CommunityResponseDTO.from(savedPost);
    }

    public CommunityPageResponseDTO getPosts(String region, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "postId")
        );

        Page<CommunityResponseDTO> pageResult = communityPostRepository
                .searchPosts(region, keyword, pageable)
                .map(CommunityResponseDTO::from);

        return CommunityPageResponseDTO.from(pageResult);
    }

    @Transactional
    public CommunityResponseDTO getPost(Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        post.increaseViewCount();

        return CommunityResponseDTO.from(post);
    }

    @Transactional
    public CommunityResponseDTO updatePost(Long postId, Long userId, CommunityUpdateDTO request) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getUserId().equals(userId)) {
            throw new IllegalArgumentException("게시글을 수정할 권한이 없습니다.");
        }

        post.update(
                request.region(),
                request.title(),
                request.content()
        );

        return CommunityResponseDTO.from(post);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getUserId().equals(userId)) {
            throw new IllegalArgumentException("게시글을 삭제할 권한이 없습니다.");
        }

        communityPostRepository.delete(post);
    }
}