package com.chcorp.homes.recommend.controller;

import com.chcorp.homes.recommend.dto.RecommendSummaryResponse;
import com.chcorp.homes.recommend.service.RecoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecController {

    private final RecoService recoService;

    @GetMapping("/summary")
    public RecommendSummaryResponse getSummary() {
        // TODO: 인증 연결 후 실제 userId로 교체 (JWT or Session)
        return recoService.getSummary(1L);
    }
}