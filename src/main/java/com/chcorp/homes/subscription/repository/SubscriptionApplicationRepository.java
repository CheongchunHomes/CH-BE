package com.chcorp.homes.subscription.repository;

import com.chcorp.homes.subscription.entity.SubscriptionApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionApplicationRepository extends JpaRepository<SubscriptionApplication, Long> {

    boolean existsByUser_IdAndAnnouncement_AnnouncementId(Long userId, Long announcementId);
}
