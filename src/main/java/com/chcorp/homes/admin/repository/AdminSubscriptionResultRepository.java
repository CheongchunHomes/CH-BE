package com.chcorp.homes.admin.repository;

import com.chcorp.homes.subscription.entity.SubscriptionApplication;
import com.chcorp.homes.subscription.entity.SubscriptionApplicationStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminSubscriptionResultRepository extends JpaRepository<SubscriptionApplication, Long> {

    @EntityGraph(attributePaths = {"user", "announcement"})
    List<SubscriptionApplication> findAllByOrderByIdDesc();

    @EntityGraph(attributePaths = {"user", "announcement"})
    List<SubscriptionApplication> findByUser_IdOrderByIdDesc(Long userId);

    @EntityGraph(attributePaths = {"user", "announcement"})
    List<SubscriptionApplication> findByStatusOrderByIdDesc(SubscriptionApplicationStatus status);

    @EntityGraph(attributePaths = {"user", "announcement"})
    List<SubscriptionApplication> findByUser_IdAndStatusOrderByIdDesc(Long userId, SubscriptionApplicationStatus status);
}
