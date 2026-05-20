package com.chcorp.homes.subscription.controller;

import com.chcorp.homes.subscription.dto.SubscriptionAnnouncementImportResultDTO;
import com.chcorp.homes.subscription.dto.SubscriptionDTO;
import com.chcorp.homes.subscription.dto.SubscriptionHouseTypeDTO;
import com.chcorp.homes.subscription.dto.SubscriptionHouseTypeImportResultDTO;
import com.chcorp.homes.subscription.service.SubscriptionAnnouncementImportService;
import com.chcorp.homes.subscription.service.SubscriptionHouseTypeImportService;
import com.chcorp.homes.subscription.service.SubscriptionHouseTypeService;
import com.chcorp.homes.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SubscriptionHouseTypeService subscriptionHouseTypeService;
    private final SubscriptionHouseTypeImportService subscriptionHouseTypeImportService;
    private final SubscriptionAnnouncementImportService subscriptionAnnouncementImportService;

    // 청약 공고 목록 조회
    @GetMapping(params = "!announcementId")
    public List<SubscriptionDTO> getAnnouncements(
            @RequestParam(required = false) String recruitmentType
    ) {
        return subscriptionService.getAnnouncements(recruitmentType);
    }

    // 공고별 주택형/타입 목록 조회
    @GetMapping(params = "announcementId")
    public List<SubscriptionHouseTypeDTO> getHouseTypes(
            @RequestParam Long announcementId
    ) {
        return subscriptionHouseTypeService.getHouseTypes(announcementId);
    }

    // 청약홈 APT 공고 기본정보 수집 및 저장
    @PostMapping("/import/applyhome-apt")
    public SubscriptionAnnouncementImportResultDTO importApplyhomeAptAnnouncements() {
        return subscriptionAnnouncementImportService.importApplyhomeAptAnnouncements();
    }

    // 청약홈 APT 주택형/타입 데이터 수집 및 저장
    @PostMapping("/house-types/import/applyhome-apt")
    public SubscriptionHouseTypeImportResultDTO importApplyhomeAptHouseTypes() {
        return subscriptionHouseTypeImportService.importApplyhomeAptHouseTypes();
    }
}