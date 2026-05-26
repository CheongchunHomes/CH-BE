package com.chcorp.homes.admin.repository;

import com.chcorp.homes.admin.entity.AdminSubscriptionApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminSubscriptionResultRepository extends JpaRepository<AdminSubscriptionApplication, Long> {

    List<AdminSubscriptionApplication> findByUserId(Long userId);
    List<AdminSubscriptionApplication> findByStatus(String status);

}