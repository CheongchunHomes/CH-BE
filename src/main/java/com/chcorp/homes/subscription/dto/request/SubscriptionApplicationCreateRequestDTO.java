package com.chcorp.homes.subscription.dto.request;

public record SubscriptionApplicationCreateRequestDTO(
        Long announcementId,
        Long supplyId,
        String housingType,
        String applicantName,
        String postalCode,
        String address,
        String detailAddress
) {
}
