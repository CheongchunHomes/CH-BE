package com.chcorp.homes.announcements.service;

import com.chcorp.homes.announcements.dto.AdminAnnouncementRequestDTO;
import com.chcorp.homes.announcements.entity.Announcement;
import com.chcorp.homes.announcements.repository.AnnouncementRepository;
import com.chcorp.homes.announcements.repository.AnnouncementScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminAnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementService announcementService;
    private final ApplyhomeAnnouncementService applyhomeAnnouncementService;
    private final AnnouncementScrapRepository announcementScrapRepository;
    private final AnnouncementCoordinateService announcementCoordinateService;

    // ==================
    // 목록 조회
    // 관리자 페이지에서는 isVisible 여부와 상관없이 전체 공고를 조회
    // ==================
    @Transactional(readOnly = true)
    public Page<Announcement> getList(
            String keyword,
            String region,
            String status,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "announcementId")
        );

        keyword = normalize(keyword);
        region = normalize(region);
        status = normalize(status);

        // 검색어 + 지역 + 상태
        if (keyword != null && region != null && status != null) {
            return announcementRepository.searchByKeywordAndRegionAndStatus(
                    keyword,
                    region,
                    status,
                    pageable
            );
        }

        // 검색어 + 지역
        if (keyword != null && region != null) {
            return announcementRepository.searchByKeywordAndRegion(
                    keyword,
                    region,
                    pageable
            );
        }

        // 검색어 + 상태
        if (keyword != null && status != null) {
            return announcementRepository.searchByKeywordAndStatus(
                    keyword,
                    status,
                    pageable
            );
        }

        // 검색어
        if (keyword != null) {
            return announcementRepository.searchByKeyword(keyword, pageable);
        }

        // 지역 + 상태
        if (region != null && status != null) {
            return announcementRepository.findByRegionContainingIgnoreCaseAndStatus(
                    region,
                    status,
                    pageable
            );
        }

        // 지역
        if (region != null) {
            return announcementRepository.findByRegionContainingIgnoreCase(region, pageable);
        }

        // 상태
        if (status != null) {
            return announcementRepository.findByStatus(status, pageable);
        }

        return announcementRepository.findAll(pageable);
    }

    // ==================
    // 단건 조회
    // ==================
    @Transactional(readOnly = true)
    public Announcement getOne(Long id) {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공고를 찾을 수 없습니다."));
    }

    // ==================
    // 등록
    // ==================
    @Transactional
    public void register(AdminAnnouncementRequestDTO dto) {
        Announcement announcement = Announcement.builder()
                .title(dto.getTitle())
                .region(dto.getRegion())
                .recuitmentType(dto.getRecuitmentType())
                .targetType(dto.getTargetType())
                .status(dto.getStatus())
                .address(dto.getAddress())
                .content(dto.getContent())
                .sourceUrl(dto.getSourceUrl())
                .isVisible(dto.getIsVisible() != null ? dto.getIsVisible() : true)
                .applyStartDate(dto.getApplyStartDate())
                .applyEndDate(dto.getApplyEndDate())
                .sourceType("관리자등록")
                .build();

        announcementRepository.save(announcement);
    }

    // ==================
    // 수정
    // ==================
    @Transactional
    public void update(Long id, AdminAnnouncementRequestDTO dto) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공고를 찾을 수 없습니다."));

        announcement.updateAdminFields(
                dto.getTitle(),
                dto.getRegion(),
                dto.getRecuitmentType(),
                dto.getTargetType(),
                dto.getStatus(),
                dto.getAddress(),
                dto.getContent(),
                dto.getSourceUrl(),
                dto.getIsVisible() != null ? dto.getIsVisible() : announcement.getIsVisible(),
                dto.getApplyStartDate(),
                dto.getApplyEndDate()
        );
    }

    // ==================
    // 노출 여부 변경
    // ==================
    @Transactional
    public void updateVisibility(Long id, Boolean isVisible) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공고를 찾을 수 없습니다."));

        announcement.updateVisibility(isVisible);
    }

    // ==================
    // 삭제
    // 관리자등록 공고라도 스크랩되어 있으면 실제 삭제하지 않고 숨김처리
    // ==================
    @Transactional
    public void delete(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공고를 찾을 수 없습니다."));

        boolean hasScrap = announcementScrapRepository
                .existsByAnnouncement_AnnouncementId(id);

        // 외부 API 공고는 실제 삭제하지 않고 숨김처리
        if (!"관리자등록".equals(announcement.getSourceType())) {
            announcement.updateVisibility(false);
            return;
        }

        // 관리자등록 공고라도 스크랩되어 있으면 실제 삭제하지 않고 숨김처리
        if (hasScrap) {
            announcement.updateVisibility(false);
            return;
        }

        // 관리자등록 공고이고 스크랩도 없을 때만 실제 삭제
        announcementRepository.delete(announcement);
    }

    // =========================
    // 외부 API 트리거
    // =========================
    @Transactional
    public void triggerFetchAllRegions() {
        announcementService.fetchAllRegions();
        announcementCoordinateService.updateMissingCoordinates();
    }

    @Transactional
    public void triggerFetchSale() {
        announcementService.fetchSaleAnnouncements();
        announcementCoordinateService.updateMissingCoordinates();
    }

    @Transactional
    public void triggerFetchApplyhome() {
        applyhomeAnnouncementService.fetchApplyhome();
        announcementCoordinateService.updateMissingCoordinates();
    }

    // =========================
    // 공통 문자열 정리
    // =========================
    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        value = value.trim();

        if (value.isBlank()) {
            return null;
        }

        return value;
    }
}