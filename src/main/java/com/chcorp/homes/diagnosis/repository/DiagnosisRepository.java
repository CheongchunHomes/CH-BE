package com.chcorp.homes.diagnosis.repository;

import com.chcorp.homes.diagnosis.entity.DiagnosisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DiagnosisRepository extends JpaRepository<DiagnosisResult, Long> {
    Optional<DiagnosisResult>findByUserId(Long userId);
}
