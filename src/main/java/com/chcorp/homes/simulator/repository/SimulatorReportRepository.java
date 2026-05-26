package com.chcorp.homes.simulator.repository;

import com.chcorp.homes.simulator.entity.SimulatorReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SimulatorReportRepository extends JpaRepository<SimulatorReport, Long> {

    // 유저 ID로 가장 최근 리포트 조회
    Optional<SimulatorReport> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}