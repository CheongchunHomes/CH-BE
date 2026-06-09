package com.chcorp.homes.simulator.service;

import com.chcorp.homes.simulator.dto.SimulatorReportRequestDto;
import com.chcorp.homes.simulator.dto.SimulatorReportResponseDto;
import com.chcorp.homes.simulator.entity.SimulatorReport;
import com.chcorp.homes.simulator.repository.SimulatorReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 시뮬레이터 리포트 서비스
 * AI 분석 결과 저장 및 조회를 담당한다
 */
@Service
@RequiredArgsConstructor
public class SimulatorReportService {

    private final SimulatorReportRepository simulatorReportRepository;

    // 리포트 저장 — 유저당 최신 1개 유지 (기존 리포트 덮어쓰기)
    @Transactional
    public void save(Long userId, SimulatorReportRequestDto dto) {
        simulatorReportRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .ifPresentOrElse(
                        existing -> existing.update(dto),
                        () -> simulatorReportRepository.save(
                                SimulatorReport.builder()
                                        .userId(userId)
                                        .assetSnapshot(dto.assetSnapshot())
                                        .housingSnapshot(dto.housingSnapshot())
                                        .loanSnapshot(dto.loanSnapshot())
                                        .scoreSnapshot(dto.scoreSnapshot())
                                        .aiResult(dto.aiResult())
                                        .aiPrompt(dto.aiPrompt())
                                        .build()
                        )
                );
    }

    // 내 최근 리포트 조회
    @Transactional(readOnly = true)
    public Optional<SimulatorReportResponseDto> getMyReport(Long userId) {
        return simulatorReportRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .map(r -> new SimulatorReportResponseDto(
                        r.getAssetSnapshot(),
                        r.getHousingSnapshot(),
                        r.getLoanSnapshot(),
                        r.getScoreSnapshot(),
                        r.getAiResult()
                ));
    }
}