package com.chcorp.homes.notice.repository;

import com.chcorp.homes.notice.entity.Notice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    @Override
    @Query("select n from Notice n where n.category <> '커뮤니티'")
    List<Notice> findAll(Sort sort);

    List<Notice> findByCategoryNotOrderByNoticeIdDesc(String category);

    List<Notice> findTop3ByCategoryOrderByCreatedAtDesc(String category);

    List<Notice> findByCategoryOrderByCreatedAtDesc(String category);
}
