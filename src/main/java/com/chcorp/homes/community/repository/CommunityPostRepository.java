package com.chcorp.homes.community.repository;

import com.chcorp.homes.community.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    List<CommunityPost> findAllByOrderByCreatedAtDesc();

    List<CommunityPost> findByRegionOrderByCreatedAtDesc(String region);
}