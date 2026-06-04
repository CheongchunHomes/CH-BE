package com.chcorp.homes.notice.service;

import com.chcorp.homes.notice.dto.NoticeCreateRequestDTO;
import com.chcorp.homes.notice.dto.NoticeResponseDTO;
import com.chcorp.homes.notice.entity.Notice;
import com.chcorp.homes.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    public static final String COMMUNITY_CATEGORY = "커뮤니티";

    private final NoticeRepository noticeRepository;

    public List<NoticeResponseDTO> getNotices() {
        return noticeRepository.findByCategoryNotOrderByNoticeIdDesc(COMMUNITY_CATEGORY)
                .stream()
                .map(NoticeResponseDTO::from)
                .toList();
    }

    public List<NoticeResponseDTO> getLatestCommunityNotices() {
        return noticeRepository.findTop3ByCategoryOrderByCreatedAtDesc(COMMUNITY_CATEGORY)
                .stream()
                .map(NoticeResponseDTO::from)
                .toList();
    }

    public List<NoticeResponseDTO> getCommunityNoticesForAdmin() {
        return noticeRepository.findByCategoryOrderByCreatedAtDesc(COMMUNITY_CATEGORY)
                .stream()
                .map(NoticeResponseDTO::from)
                .toList();
    }

    @Transactional
    public NoticeResponseDTO getNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));

        notice.increaseViewCount();

        return NoticeResponseDTO.from(notice);
    }

    @Transactional
    public NoticeResponseDTO createNoticeFromAdmin(NoticeCreateRequestDTO request) {
        Notice notice = Notice.builder()
                .category(toGeneralCategory(request.category()))
                .title(request.title())
                .summary(request.summary())
                .content(request.content())
                .important(Boolean.TRUE.equals(request.important()))
                .viewCount(0)
                .build();

        Notice saved = noticeRepository.save(notice);

        return NoticeResponseDTO.from(saved);
    }

    @Transactional
    public NoticeResponseDTO createCommunityNoticeFromAdmin(NoticeCreateRequestDTO request) {
        Notice notice = Notice.builder()
                .category(COMMUNITY_CATEGORY)
                .title(request.title())
                .summary(request.summary())
                .content(request.content())
                .important(Boolean.TRUE.equals(request.important()))
                .viewCount(0)
                .build();

        Notice saved = noticeRepository.save(notice);

        return NoticeResponseDTO.from(saved);
    }

    @Transactional
    public NoticeResponseDTO updateNoticeFromAdmin(Long noticeId, NoticeCreateRequestDTO request) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));

        notice.updateFromAdmin(
                toGeneralCategory(request.category()),
                request.title(),
                request.summary(),
                request.content(),
                Boolean.TRUE.equals(request.important())
        );

        return NoticeResponseDTO.from(notice);
    }

    @Transactional
    public NoticeResponseDTO updateCommunityNoticeFromAdmin(Long noticeId, NoticeCreateRequestDTO request) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));

        if (!COMMUNITY_CATEGORY.equals(notice.getCategory())) {
            throw new IllegalArgumentException("커뮤니티 공지사항이 아닙니다.");
        }

        notice.updateFromAdmin(
                COMMUNITY_CATEGORY,
                request.title(),
                request.summary(),
                request.content(),
                Boolean.TRUE.equals(request.important())
        );

        return NoticeResponseDTO.from(notice);
    }

    @Transactional
    public void deleteNoticeFromAdmin(Long noticeId) {
        if (!noticeRepository.existsById(noticeId)) {
            throw new IllegalArgumentException("공지사항을 찾을 수 없습니다.");
        }

        noticeRepository.deleteById(noticeId);
    }

    @Transactional
    public void deleteCommunityNoticeFromAdmin(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다."));

        if (!COMMUNITY_CATEGORY.equals(notice.getCategory())) {
            throw new IllegalArgumentException("커뮤니티 공지사항이 아닙니다.");
        }

        noticeRepository.delete(notice);
    }

    private String blankToDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private String toGeneralCategory(String category) {
        String normalizedCategory = blankToDefault(category, "운영자 안내");
        return COMMUNITY_CATEGORY.equals(normalizedCategory) ? "운영자 안내" : normalizedCategory;
    }
}
