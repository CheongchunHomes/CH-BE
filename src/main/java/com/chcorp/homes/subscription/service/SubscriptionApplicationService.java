package com.chcorp.homes.subscription.service;

import com.chcorp.homes.announcements.entity.Announcement;
import com.chcorp.homes.announcements.repository.AnnouncementRepository;
import com.chcorp.homes.subscription.dto.request.SubscriptionApplicationCreateRequestDTO;
import com.chcorp.homes.subscription.dto.response.SubscriptionApplicationResponseDTO;
import com.chcorp.homes.subscription.entity.SubscriptionApplication;
import com.chcorp.homes.subscription.entity.SubscriptionApplicationStatus;
import com.chcorp.homes.subscription.repository.SubscriptionApplicationRepository;
import com.chcorp.homes.users.entity.User;
import com.chcorp.homes.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SubscriptionApplicationService {

    private final SubscriptionApplicationRepository subscriptionApplicationRepository;
    private final UserRepository userRepository;
    private final AnnouncementRepository announcementRepository;

    @Transactional
    public SubscriptionApplicationResponseDTO apply(
            Long currentUserId,
            SubscriptionApplicationCreateRequestDTO request
    ) {
        validateCurrentUserId(currentUserId);
        validateCreateRequest(request);

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        Announcement announcement = announcementRepository.findById(request.announcementId())
                .orElseThrow(() -> new NoSuchElementException("청약 공고를 찾을 수 없습니다."));

        if (subscriptionApplicationRepository.existsByUser_IdAndAnnouncement_AnnouncementId(
                currentUserId,
                request.announcementId()
        )) {
            throw new IllegalStateException("이미 신청한 청약 공고입니다.");
        }

        SubscriptionApplication application = SubscriptionApplication.builder()
                .user(user)
                .announcement(announcement)
                .status(SubscriptionApplicationStatus.PENDING)
                .supplyId(request.supplyId())
                .housingType(normalize(request.housingType()))
                .applicantName(normalize(request.applicantName()))
                .postalCode(normalize(request.postalCode()))
                .address(normalize(request.address()))
                .detailAddress(normalize(request.detailAddress()))
                .build();

        return SubscriptionApplicationResponseDTO.from(
                subscriptionApplicationRepository.save(application)
        );
    }

    private void validateCurrentUserId(Long currentUserId) {
        if (currentUserId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
    }

    private void validateCreateRequest(SubscriptionApplicationCreateRequestDTO request) {
        if (request == null || request.announcementId() == null) {
            throw new IllegalArgumentException("announcementId가 필요합니다.");
        }
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
