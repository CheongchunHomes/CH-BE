package com.chcorp.homes.banner.repository;

import com.chcorp.homes.banner.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    @Query("SELECT b FROM Banner b WHERE b.isVisible = true " +
            "AND :now BETWEEN b.startDate AND b.endDate " +
            "ORDER BY b.sortOrder ASC, b.id DESC")
    List<Banner> findActiveBanners(@Param("now") LocalDateTime now);

    // 관리자 화면도 우선순위 정렬 기준으로 조회
    List<Banner> findAllByOrderBySortOrderAscIdDesc();
}