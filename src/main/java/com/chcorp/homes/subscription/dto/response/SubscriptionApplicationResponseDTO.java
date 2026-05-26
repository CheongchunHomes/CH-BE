package com.chcorp.homes.subscription.dto.response;

import com.chcorp.homes.subscription.entity.SubscriptionApplication;
import com.chcorp.homes.subscription.entity.SubscriptionApplicationStatus;

import java.time.Instant;

public record SubscriptionApplicationResponseDTO(
        Long id,
        Long userId,
        Long announcementId,
        SubscriptionApplicationStatus status,
        Long supplyId,
        String housingType,
        String applicantName,
        String postalCode,
        String address,
        String detailAddress,
        Instant createdAt,
        Instant updatedAt
) {

    public static SubscriptionApplicationResponseDTO from(SubscriptionApplication application) {
        return new SubscriptionApplicationResponseDTO(
                application.getId(),
                application.getUser().getId(),
                application.getAnnouncement().getAnnouncementId(),
                application.getStatus(),
                application.getSupplyId(),
                application.getHousingType(),
                application.getApplicantName(),
                application.getPostalCode(),
                application.getAddress(),
                application.getDetailAddress(),
                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }
}
