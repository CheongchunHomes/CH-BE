package com.chcorp.homes.recommend.controller;

import com.chcorp.homes.recommend.dto.RecommendSummaryResponse;
import com.chcorp.homes.recommend.service.RecoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecController {

    private final RecoService recoService;

    @GetMapping("/summary")
    public ResponseEntity<RecommendSummaryResponse> getSummary(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(recoService.getSummary(userId));
    }
}