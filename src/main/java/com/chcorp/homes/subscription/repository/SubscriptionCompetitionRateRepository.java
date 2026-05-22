package com.chcorp.homes.subscription.repository;

import com.chcorp.homes.subscription.entity.SubscriptionCompetitionRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionCompetitionRateRepository extends JpaRepository<SubscriptionCompetitionRate, Long> {

    // 특정 공고에 해당 순위 경쟁률 데이터가 있는지 확인한다.
    boolean existsByAnnouncementIdAndRankNo(Long announcementId, Integer rankNo);
}