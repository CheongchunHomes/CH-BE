package com.chcorp.homes.banner.service;

import com.chcorp.homes.banner.dto.request.BannerRequestDto;
import com.chcorp.homes.banner.dto.response.BannerResponseDto;
import com.chcorp.homes.banner.entity.Banner;
import com.chcorp.homes.banner.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BannerService {

    private final BannerRepository bannerRepository;

    // 관리자 화면용 전체 배너 조회 (노출 순서 오름차순, ID 내림차순)
    public List<BannerResponseDto> getAllBannersForAdmin() {
        return bannerRepository.findAllByOrderBySortOrderAscIdDesc().stream()
                .map(BannerResponseDto::from)
                .toList();
    }

    // 배너 단건 조회 (수정 폼 진입 시)
    public BannerResponseDto getOne(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배너입니다. ID: " + id));
        return BannerResponseDto.from(banner);
    }

    // 배너 신규 등록 (종료일 유효성 검증 포함)
    @Transactional
    public void createBanner(BannerRequestDto dto) {
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("종료일은 시작일보다 빠를 수 없습니다.");
        }

        Banner banner = Banner.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .noticeId(dto.getNoticeId())
                .linkUrl(dto.getLinkUrl())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .sortOrder(dto.getSortOrder())
                .isVisible(dto.isVisible())
                .build();

        bannerRepository.save(banner);
    }

    // 배너 수정
    @Transactional
    public void updateBanner(Long id, BannerRequestDto dto) {
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("종료일은 시작일보다 빠를 수 없습니다.");
        }

        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배너입니다. ID: " + id));

        banner.update(
                dto.getTitle(),
                dto.getContent(),
                dto.getNoticeId(),
                dto.getLinkUrl(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getSortOrder(),
                dto.isVisible()
        );
    }

    // 배너 삭제
    @Transactional
    public void deleteBanner(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배너입니다. ID: " + id));
        bannerRepository.delete(banner);
    }

    // 배너 노출 상태 토글 (노출 ↔ 중지)
    @Transactional
    public void toggleBannerVisibility(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배너입니다. ID: " + id));
        banner.toggleVisibility();
    }

    // 프론트 메인 화면용 활성 배너 조회
    // isVisible = true && 현재 시각이 startDate ~ endDate 사이인 배너만 반환
    public List<BannerResponseDto> getActiveBannersToFront() {
        return bannerRepository.findActiveBanners(LocalDateTime.now()).stream()
                .map(BannerResponseDto::from)
                .toList();
    }

    // 이미 배너에 연결된 noticeId 목록 조회 (드롭다운 [배너연결] 표시용)
    public List<Long> getUsedNoticeIds() {
        return bannerRepository.findAllByOrderBySortOrderAscIdDesc()
                .stream()
                .map(Banner::getNoticeId)
                .filter(id -> id != null)
                .toList();
    }
}