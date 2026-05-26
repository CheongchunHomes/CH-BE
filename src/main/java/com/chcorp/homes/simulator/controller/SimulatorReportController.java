package com.chcorp.homes.simulator.controller;

import com.chcorp.homes.simulator.dto.SimulatorReportRequestDto;
import com.chcorp.homes.simulator.dto.SimulatorReportResponseDto;
import com.chcorp.homes.simulator.entity.SimulatorReport;
import com.chcorp.homes.simulator.service.SimulatorReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 시뮬레이터 리포트 컨트롤러
 * AI 분석 결과 저장 및 조회 API를 제공한다
 */
@RestController
@RequestMapping("/simulator/reports")
@RequiredArgsConstructor
public class SimulatorReportController {

    private final SimulatorReportService simulatorReportService;

    // 리포트 저장
    @PostMapping
    public ResponseEntity<Void> save(
            Authentication authentication,
            @RequestBody SimulatorReportRequestDto dto
    ) {
        Long userId = Long.valueOf(authentication.getName());
        simulatorReportService.save(userId, dto);
        return ResponseEntity.ok().build();
    }

    // 내 최근 리포트 조회
    @GetMapping("/me")
    public ResponseEntity<SimulatorReportResponseDto> getMyReport(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        SimulatorReportResponseDto report = simulatorReportService.getMyReport(userId);
        return report != null ? ResponseEntity.ok(report) : ResponseEntity.noContent().build();
    }
}