package com.chcorp.homes.subscription.repository;

import com.chcorp.homes.subscription.entity.Announcem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Announcem, Long> {

    List<Announcem> findByRecruitmentTypeAndIsVisibleTrueOrderByApplyStartDateAsc(String recruitmentType);

    List<Announcem> findBySourceTypeAndIsVisibleTrueOrderByApplyStartDateAsc(String sourceType);

    List<Announcem> findByIsVisibleTrueOrderByApplyStartDateAsc();

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