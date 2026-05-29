package com.chcorp.homes.subscription.service;

import com.chcorp.homes.announcements.entity.Announcement;
import com.chcorp.homes.announcements.repository.AnnouncementRepository;
import com.chcorp.homes.subscription.dto.SubscriptionDTO;
import com.chcorp.homes.subscription.dto.SubscriptionGeocodeResultDTO;
import com.chcorp.homes.subscription.dto.SubscriptionMapDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private static final String CATEGORY_APT = "apt";
    private static final String CATEGORY_OFFICE = "office";
    private static final String CATEGORY_PUBLIC = "public";

    private static final String APPLY_TYPE_SPECIAL = "SPECIAL";
    private static final String APPLY_TYPE_GENERAL = "GENERAL";
    private static final String APPLY_TYPE_REMAIN = "REMAIN";

    private static final String SOURCE_APPLYHOME = "청약홈";
    private static final String SOURCE_MYHOME_PUBLIC_SALE = "마이홈포털-공공분양주택";
    private static final String SOURCE_MYHOME_PUBLIC_RENT = "마이홈포털-공공임대주택";

    private static final String RECRUITMENT_APT = "아파트";
    private static final String RECRUITMENT_OFFICE = "도시형/오피스텔/생활숙박시설/민간임대";

    private final AnnouncementRepository announcementRepository;
    private final KakaoAddressGeocodingService kakaoAddressGeocodingService;

    public List<SubscriptionDTO> getAnnouncements(String category, String recruitmentType, String applyType) {
        LocalDate today = LocalDate.now();

        Pageable pageable = PageRequest.of(
                0,
                100,
                Sort.by(Sort.Direction.ASC, "applyEndDate")
        );

        Page<Announcement> announcementPage = findAnnouncementsByCategory(
                category,
                recruitmentType,
                applyType,
                today,
                pageable
        );

        return announcementPage.getContent()
                .stream()
                .map(SubscriptionDTO::from)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<SubscriptionMapDTO> getMapAnnouncements() {
        LocalDate today = LocalDate.now();

        return announcementRepository.findApplyingNowMapAnnouncements(today)
                .stream()
                .map(SubscriptionMapDTO::from)
                .toList();
    }

    @Transactional
    public SubscriptionGeocodeResultDTO geocodeMissingCoordinates(int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 500);
        Pageable pageable = PageRequest.of(0, safeLimit);
        List<Announcement> targets = announcementRepository.findGeocodeTargets(pageable);

        int successCount = 0;
        int failedCount = 0;
        int skippedCount = 0;

        for (Announcement announcement : targets) {
            if (announcement.getAddress() == null || announcement.getAddress().isBlank()) {
                skippedCount++;
                continue;
            }

            var coordinates = kakaoAddressGeocodingService.geocode(announcement.getAddress());

            if (coordinates.isEmpty()) {
                failedCount++;
                continue;
            }

            announcement.updateCoordinates(
                    coordinates.get().latitude(),
                    coordinates.get().longitude()
            );
            successCount++;
        }

        return new SubscriptionGeocodeResultDTO(
                targets.size(),
                successCount,
                failedCount,
                skippedCount
        );
    }

    private Page<Announcement> findAnnouncementsByCategory(
            String category,
            String recruitmentType,
            String applyType,
            LocalDate today,
            Pageable pageable
    ) {
        if (CATEGORY_APT.equals(category)) {
            return findAptAnnouncementsByApplyType(applyType, today, pageable);
        }

        if (CATEGORY_OFFICE.equals(category)) {
            return announcementRepository
                    .findBySourceTypeAndRecuitmentTypeAndIsVisibleTrueAndApplyEndDateGreaterThanEqual(
                            SOURCE_APPLYHOME,
                            RECRUITMENT_OFFICE,
                            today,
                            pageable
                    );
        }

        if (CATEGORY_PUBLIC.equals(category)) {
            return announcementRepository
                    .findBySourceTypeInAndIsVisibleTrueAndApplyEndDateGreaterThanEqual(
                            List.of(SOURCE_MYHOME_PUBLIC_SALE, SOURCE_MYHOME_PUBLIC_RENT),
                            today,
                            pageable
                    );
        }

        if (recruitmentType != null && !recruitmentType.isBlank()) {
            return announcementRepository
                    .findByRecuitmentTypeAndIsVisibleTrueAndApplyEndDateGreaterThanEqual(
                            recruitmentType,
                            today,
                            pageable
                    );
        }

        return announcementRepository
                .findByIsVisibleTrueAndApplyEndDateGreaterThanEqual(today, pageable);
    }

    private Page<Announcement> findAptAnnouncementsByApplyType(
            String applyType,
            LocalDate today,
            Pageable pageable
    ) {
        // 특별공급 물량이 있는 APT 공고
        if (APPLY_TYPE_SPECIAL.equals(applyType)) {
            return announcementRepository.findAptSpecialSupplyAnnouncements(
                    SOURCE_APPLYHOME,
                    RECRUITMENT_APT,
                    today,
                    pageable
            );
        }

        // 1순위/2순위 묶음: 일반공급 물량이 있는 APT 공고
        if (APPLY_TYPE_GENERAL.equals(applyType)) {
            return announcementRepository.findAptGeneralAnnouncements(
                    SOURCE_APPLYHOME,
                    RECRUITMENT_APT,
                    today,
                    pageable
            );
        }

        // 잔여세대는 아직 API 연결 전
        if (APPLY_TYPE_REMAIN.equals(applyType)) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        return findApplyhomeAptAnnouncements(today, pageable);
    }

    private Page<Announcement> findApplyhomeAptAnnouncements(
            LocalDate today,
            Pageable pageable
    ) {
        return announcementRepository
                .findBySourceTypeAndRecuitmentTypeAndIsVisibleTrueAndApplyEndDateGreaterThanEqual(
                        SOURCE_APPLYHOME,
                        RECRUITMENT_APT,
                        today,
                        pageable
                );
    }
}