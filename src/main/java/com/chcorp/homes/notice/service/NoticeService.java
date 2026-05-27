package com.chcorp.homes.notice.service;

import com.chcorp.homes.notice.dto.NoticeCreateRequestDTO;
import com.chcorp.homes.notice.dto.NoticeResponseDTO;
import com.chcorp.homes.notice.entity.Notice;
import com.chcorp.homes.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public List<NoticeResponseDTO> getNotices() {
        return noticeRepository.findAll(Sort.by(Sort.Direction.DESC, "noticeId"))
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
                .category(blankToDefault(request.category(), "운영자 안내"))
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
                blankToDefault(request.category(), "운영자 안내"),
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

    private String blankToDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}