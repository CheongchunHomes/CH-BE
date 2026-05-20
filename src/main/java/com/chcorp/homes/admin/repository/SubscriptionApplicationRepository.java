package com.chcorp.homes.admin.repository;

import com.chcorp.homes.admin.entity.SubscriptionApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionApplicationRepository
        extends JpaRepository<SubscriptionApplication, Long> {

    List<SubscriptionApplication> findByUserId(Long userId);
    List<SubscriptionApplication> findByStatus(String status);

}
