package com.chcorp.homes.announcement.service;

import com.chcorp.homes.announcement.dto.AnnouncementListDTO;
import com.chcorp.homes.announcement.entity.Announcement;
import com.chcorp.homes.announcement.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public List<AnnouncementListDTO> getAnnouncements(String recruitmentType) {
        List<Announcement> announcements;

        if (recruitmentType == null || recruitmentType.isBlank()) {
            announcements = announcementRepository.findByIsVisibleTrueOrderByApplyStartDateAsc();
        } else {
            announcements = announcementRepository
                    .findByRecruitmentTypeAndIsVisibleTrueOrderByApplyStartDateAsc(recruitmentType);
        }

        return announcements.stream()
                .map(AnnouncementListDTO::from)
                .toList();
    }
}