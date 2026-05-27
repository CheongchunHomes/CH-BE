package com.chcorp.homes.simulator.repository;

import com.chcorp.homes.simulator.entity.AssetPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetPlanRepository extends JpaRepository<AssetPlan, Long> {

    /* 유저의 플랜 전체 조회 (생성일 내림차순) */
    List<AssetPlan> findByUserIdOrderByCreatedAtDesc(Long userId);
}