package com.chcorp.homes.subscription.repository;

import com.chcorp.homes.subscription.entity.SubscriptionApplication;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionApplicationRepository extends JpaRepository<SubscriptionApplication, Long> {

    boolean existsByUser_IdAndAnnouncement_AnnouncementId(Long userId, Long announcementId);

    @EntityGraph(attributePaths = {"user", "announcement"})
    Optional<SubscriptionApplication> findFirstByUser_IdOrderByUpdatedAtDescIdDesc(Long userId);
}
