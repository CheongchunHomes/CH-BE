package com.chcorp.homes.subscription.controller;

import com.chcorp.homes.subscription.dto.SubscriptionAnnouncementImportResultDTO;
import com.chcorp.homes.subscription.dto.SubscriptionDTO;
import com.chcorp.homes.subscription.dto.SubscriptionGeocodeResultDTO;
import com.chcorp.homes.subscription.dto.SubscriptionHouseTypeDTO;
import com.chcorp.homes.subscription.dto.SubscriptionMapDTO;
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
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String recruitmentType,
            @RequestParam(required = false) String applyType
    ) {
        return subscriptionService.getAnnouncements(category, recruitmentType, applyType);
    }


    // 지도에 표시할 청약 공고 목록 조회
    @GetMapping("/map")
    public List<SubscriptionMapDTO> getMapAnnouncements() {
        return subscriptionService.getMapAnnouncements();
    }

    // 주소만 있고 좌표가 없는 청약 공고의 위도/경도를 일괄 저장
    @PostMapping("/admin/geocode")
    public SubscriptionGeocodeResultDTO geocodeMissingCoordinates(
            @RequestParam(defaultValue = "100") int limit
    ) {
        return subscriptionService.geocodeMissingCoordinates(limit);
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
