package com.chcorp.homes.subscription.dto;

import com.chcorp.homes.announcements.entity.Announcement;

import java.math.BigDecimal;
import java.util.List;

/**
 * 지도 화면에서 청약 공고를 매물과 같은 형태로 보여주기 위한 DTO입니다.
 */
public record SubscriptionMapDTO(
        Long id,
        String title,
        String address,
        String region,
        Double latitude,
        Double longitude,
        String category,
        String dealType,
        Integer depositAmount,
        Integer monthlyRentAmount,
        Integer maintenanceFee,
        String depositLabel,
        String monthlyRentLabel,
        String roomType,
        BigDecimal exclusiveAreaM2,
        BigDecimal supplyAreaM2,
        Integer roomCount,
        Integer bathroomCount,
        Integer floor,
        Integer totalFloor,
        String direction,
        String heatingType,
        Boolean elevatorAvailable,
        Integer totalParkingCount,
        String buildingUse,
        String moveInType,
        String moveInDate,
        String approvalDate,
        String firstRegistrationDate,
        List<String> tag,
        List<String> options,
        List<String> securityFacilities,
        String thumbnailUrl,
        String description
) {

    /**
     * Announcement 엔티티를 지도용 DTO로 변환합니다.
     */
    public static SubscriptionMapDTO from(Announcement announcement) {
        String recruitmentType = nullToDash(announcement.getRecuitmentType());
        String sourceType = nullToDash(announcement.getSourceType());
        String status = nullToDash(announcement.getStatus());

        return new SubscriptionMapDTO(
                announcement.getAnnouncementId(),
                announcement.getTitle(),
                announcement.getAddress(),
                announcement.getRegion(),
                toDouble(announcement.getLatitude()),
                toDouble(announcement.getLongitude()),
                "subscription",
                null,
                announcement.getRentGtn(),
                announcement.getMtRntchrg(),
                null,
                "청약공고",
                recruitmentType,
                recruitmentType,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                announcement.getHeatMthdNm(),
                null,
                null,
                null,
                "청약 공고",
                announcement.getMvnPrearngeYm(),
                null,
                null,
                List.of("분양공고", sourceType, recruitmentType),
                List.of(),
                List.of(),
                null,
                "출처: " + sourceType + " · 상태: " + status
        );
    }

    private static Double toDouble(BigDecimal value) {
        return value == null ? null : value.doubleValue();
    }

    private static String nullToDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}