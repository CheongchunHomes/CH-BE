package com.chcorp.homes.subscription.dto;

/**
 * 청약 공고 주소 좌표 변환 결과 DTO입니다.
 */
public record SubscriptionGeocodeResultDTO(
        int targetCount,
        int successCount,
        int failedCount,
        int skippedCount
) {
}