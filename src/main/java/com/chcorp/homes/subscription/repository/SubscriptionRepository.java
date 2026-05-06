package com.chcorp.homes.subscription.repository;

import com.chcorp.homes.subscription.entity.Announcem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Announcem, Long> {

    List<Announcem> findByRecruitmentTypeAndIsVisibleTrueOrderByApplyStartDateAsc(String recruitmentType);

    List<Announcem> findBySourceTypeAndIsVisibleTrueOrderByApplyStartDateAsc(String sourceType);

    List<Announcem> findByIsVisibleTrueOrderByApplyStartDateAsc();
}