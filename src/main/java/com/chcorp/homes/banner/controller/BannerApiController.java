package com.chcorp.homes.banner.controller;

import com.chcorp.homes.banner.dto.response.BannerResponseDto;
import com.chcorp.homes.banner.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 프론트 메인 화면용 활성 배너 API
@RestController
@RequiredArgsConstructor
@RequestMapping("/banners")
public class BannerApiController {

    private final BannerService bannerService;

    @GetMapping
    public ResponseEntity<List<BannerResponseDto>> getActiveBanners() {
        return ResponseEntity.ok(bannerService.getActiveBannersToFront());
    }
}
