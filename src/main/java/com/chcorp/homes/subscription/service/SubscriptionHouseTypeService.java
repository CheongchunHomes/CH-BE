package com.chcorp.homes.subscription.service;

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

    public List<SubscriptionHouseTypeDTO> getHouseTypes(Long announcementId) {
        return subscriptionHouseTypeRepository
                .findByAnnouncementIdOrderByHouseTypeNameAsc(announcementId)
                .stream()
                .map(SubscriptionHouseTypeDTO::from)
                .toList();
    }
}