package com.chcorp.homes.announcements.repository;

import com.chcorp.homes.announcements.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    boolean existsByExternalId(String externalId);

    Page<Announcement> findByRegion(String region, Pageable pageable);

    Page<Announcement> findByStatus(String status, Pageable pageable);

    Page<Announcement> findByRegionAndStatus(String region, String status, Pageable pageable);

    Page<Announcement> findByIsVisibleTrue(Pageable pageable);

    Page<Announcement> findByRecuitmentTypeAndIsVisibleTrue(
            String recuitmentType,
            Pageable pageable
    );
}