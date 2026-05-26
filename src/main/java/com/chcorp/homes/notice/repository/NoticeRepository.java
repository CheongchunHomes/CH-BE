package com.chcorp.homes.notice.repository;

import com.chcorp.homes.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}