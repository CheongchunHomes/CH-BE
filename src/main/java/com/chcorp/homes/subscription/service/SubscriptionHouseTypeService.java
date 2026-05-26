package com.chcorp.homes.subscription.service;

import com.chcorp.homes.announcements.entity.Announcement;
import com.chcorp.homes.announcements.repository.AnnouncementRepository;
import com.chcorp.homes.subscription.dto.SubscriptionHouseTypeDTO;
import com.chcorp.homes.subscription.repository.SubscriptionHouseTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionHouseTypeService {

    private final SubscriptionHouseTypeRepository subscriptionHouseTypeRepository;
    private final AnnouncementRepository announcementRepository;

    public List<SubscriptionHouseTypeDTO> getHouseTypes(Long announcementId) {
        List<SubscriptionHouseTypeDTO> houseTypes = subscriptionHouseTypeRepository
                .findByAnnouncementIdOrderByHouseTypeNameAsc(announcementId)
                .stream()
                .map(SubscriptionHouseTypeDTO::from)
                .toList();

        if (!houseTypes.isEmpty()) {
            return houseTypes;
        }

        return announcementRepository.findById(announcementId)
                .filter(this::isMyhomeAnnouncement)
                .map(announcement -> List.of(SubscriptionHouseTypeDTO.defaultForMyhome(announcementId)))
                .orElseGet(List::of);
    }

    private boolean isMyhomeAnnouncement(Announcement announcement) {
        String sourceType = announcement.getSourceType();
        return sourceType != null && sourceType.contains("마이홈");
    }
}
