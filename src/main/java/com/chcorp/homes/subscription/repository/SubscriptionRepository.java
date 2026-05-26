package com.chcorp.homes.subscription.repository;

import com.chcorp.homes.announcements.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Announcement, Long> {

    boolean existsByPblancNo(String pblancNo);

    @Query(
            value = """
                    SELECT announcement_id
                    FROM announcements
                    WHERE pblanc_no = :pblancNo
                    LIMIT 1
                    """,
            nativeQuery = true
    )
    Optional<Long> findAnnouncementIdByPblancNo(@Param("pblancNo") String pblancNo);
}