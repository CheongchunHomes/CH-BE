package com.chcorp.homes.diagnosis.repository;

import com.chcorp.homes.diagnosis.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    // 내 진단 히스토리 조회 (최신순)
    List<Diagnosis> findByUser_IdOrderByCreatedAtDesc(Long userId);
}