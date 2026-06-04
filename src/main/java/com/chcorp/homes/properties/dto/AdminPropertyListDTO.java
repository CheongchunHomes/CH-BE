package com.chcorp.homes.properties.dto;

import com.chcorp.homes.properties.entity.Property;

public record AdminPropertyListDTO(
        Long id,
        String title,
        String address,
        String dealType,
        Integer depositAmount,
        Integer monthlyRentAmount,
        Integer maintenanceFee,
        Long landlordUserId,
        Double latitude,
        Double longitude,
        String thumbnailUrl,
        String thumbnailPreviewUrl
) {

    public static AdminPropertyListDTO from(Property property, String thumbnailPreviewUrl) {
        return new AdminPropertyListDTO(
                property.getId(),
                property.getTitle(),
                property.getAddress(),
                property.getDealType(),
                property.getDepositAmount(),
                property.getMonthlyRentAmount(),
                property.getMaintenanceFee(),
                property.getLandlordUserId(),
                property.getLatitude(),
                property.getLongitude(),
                property.getThumbnailUrl(),
                thumbnailPreviewUrl
        );
    }
}
