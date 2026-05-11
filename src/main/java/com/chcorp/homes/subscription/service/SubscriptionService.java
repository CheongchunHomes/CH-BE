package com.chcorp.homes.subscription.service;

import com.chcorp.homes.announcements.entity.Announcement;
import com.chcorp.homes.announcements.repository.AnnouncementRepository;
import com.chcorp.homes.subscription.dto.SubscriptionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final AnnouncementRepository announcementRepository;

    public List<SubscriptionDTO> getAnnouncements(String recruitmentType) {

        Pageable pageable = PageRequest.of(
                0,
                100,
                Sort.by(Sort.Direction.ASC, "applyStartDate")
        );

        Page<Announcement> announcementPage;

        if (recruitmentType == null || recruitmentType.isBlank()) {
            announcementPage = announcementRepository.findByIsVisibleTrue(pageable);
        } else {
            announcementPage = announcementRepository
                    .findByRecuitmentTypeAndIsVisibleTrue(recruitmentType, pageable);
        }

        return announcementPage.getContent()
                .stream()
                .map(SubscriptionDTO::from)
                .toList();
    }
}