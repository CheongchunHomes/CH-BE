package com.chcorp.homes.announcements.repository;

import com.chcorp.homes.announcements.entity.AnnouncementScrap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnnouncementScrapRepository extends JpaRepository<AnnouncementScrap, Long> {

    boolean existsByUserIdAndAnnouncementAnnouncementId(
            Long userId,
            Long announcementId
    );

    Optional<AnnouncementScrap> findByUserIdAndAnnouncementAnnouncementId(
            Long userId,
            Long announcementId
    );

    List<AnnouncementScrap> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByAnnouncement_AnnouncementId(Long announcementId);
}
