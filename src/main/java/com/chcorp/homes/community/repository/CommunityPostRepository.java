package com.chcorp.homes.community.repository;

import com.chcorp.homes.community.entity.CommunityPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    @Query("""
            SELECT p
            FROM CommunityPost p
            WHERE (:region IS NULL OR :region = '' OR p.region LIKE CONCAT(:region, '%'))
              AND (
                    :keyword IS NULL
                    OR :keyword = ''
                    OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.region) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            """)
    Page<CommunityPost> searchPosts(
            @Param("region") String region,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}