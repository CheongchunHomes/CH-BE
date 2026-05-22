package com.chcorp.homes.announcements.service;

import com.chcorp.homes.announcements.dto.AnnouncementScrapDTO;
import com.chcorp.homes.announcements.entity.Announcement;
import com.chcorp.homes.announcements.entity.AnnouncementScrap;
import com.chcorp.homes.announcements.repository.AnnouncementRepository;
import com.chcorp.homes.announcements.repository.AnnouncementScrapRepository;
import com.chcorp.homes.users.entity.User;
import com.chcorp.homes.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AnnouncementScrapService {

    private final AnnouncementScrapRepository scrapRepository;
    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;

    // 공고 스크랩 등록
    // 같은 사용자가 같은 공고 스크랩한 경우 중복 저장 X 그대로 종료
    public void addScrap(Long userId, Long announcementId) {
        if (scrapRepository.existsByUserIdAndAnnouncementAnnouncementId(userId, announcementId)) {
            return;
        }

        // 현재 로그인한 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 스크랩할 공고 조회
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("공고를 찾을 수 없습니다."));

        // 숨김 처리된 공고는 새로 스크랩할 수 없도록 방지
        if (Boolean.FALSE.equals(announcement.getIsVisible())) {
            throw new RuntimeException("비공개 처리된 공고는 스크랩할 수 없습니다.");
        }

        // 스크랩 엔티티 생성 후 저장
        AnnouncementScrap scrap = AnnouncementScrap.builder()
                .user(user)
                .announcement(announcement)
                .build();

        scrapRepository.save(scrap);
    }

    // 공고 스크랩 취소
    // 로그인한 사용자의 특정 공고 스크랩 데이터 찾아 삭제
    public void removeScrap(Long userId, Long announcementId) {
        AnnouncementScrap scrap = scrapRepository
                .findByUserIdAndAnnouncementAnnouncementId(userId, announcementId)
                .orElseThrow(() -> new RuntimeException("스크랩이 존재하지 않습니다."));

        scrapRepository.delete(scrap);
    }

    // 내 스크랩 공고 목록 조회
    // 마이페이지에서 스크랩한 공고 목록 보여줄 때 사용
    @Transactional(readOnly = true)
    public List<AnnouncementScrapDTO> getMyScraps(Long userId) {
        return scrapRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(AnnouncementScrapDTO::new)
                .toList();
    }

    // 내가 스크랩한 공고 Id 목록 조회
    // 공고 리스트에서 하트 버튼이 눌린 상태인지 판단할 때 사용
    @Transactional(readOnly = true)
    public List<Long> getMyScrapAnnouncementIds(Long userId) {
        return scrapRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(scrap -> scrap.getAnnouncement().getAnnouncementId())
                .toList();
    }
}
