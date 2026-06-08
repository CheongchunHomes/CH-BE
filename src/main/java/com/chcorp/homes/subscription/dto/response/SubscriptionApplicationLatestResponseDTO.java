package com.chcorp.homes.subscription.dto.response;

import com.chcorp.homes.subscription.entity.SubscriptionApplication;

import java.time.Instant;
import java.time.LocalDateTime;

public record SubscriptionApplicationLatestResponseDTO(
        Long id,
        Long userId,
        Long announcementId,
        String announcementTitle,
        String status,
        Long supplyId,
        String housingType,
        String applicantName,
        LocalDateTime resultAt,
        Instant createdAt,
        Instant updatedAt
) {

    public static SubscriptionApplicationLatestResponseDTO empty() {
        return new SubscriptionApplicationLatestResponseDTO(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static SubscriptionApplicationLatestResponseDTO from(SubscriptionApplication application) {
        return new SubscriptionApplicationLatestResponseDTO(
                application.getId(),
                application.getUser() != null ? application.getUser().getId() : null,
                application.getAnnouncement() != null ? application.getAnnouncement().getAnnouncementId() : null,
                application.getAnnouncement() != null ? application.getAnnouncement().getTitle() : null,
                application.getStatus() != null ? application.getStatus().name() : null,
                application.getSupplyId(),
                application.getHousingType(),
                application.getApplicantName(),
                application.getResultAt(),
                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }
}
