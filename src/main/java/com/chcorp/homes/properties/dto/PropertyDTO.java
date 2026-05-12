package com.chcorp.homes.properties.dto;

import com.chcorp.homes.properties.entity.Property;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public record PropertyDTO(
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
        LocalDate moveInDate,
        LocalDate approvalDate,
        LocalDate firstRegistrationDate,

        List<String> tag,
        List<String> options,
        List<String> securityFacilities,

        String thumbnailUrl,
        String description
) {
    public static PropertyDTO from(Property property) {
        return new PropertyDTO(
                property.getId(),
                property.getTitle(),
                property.getAddress(),
                property.getRegion(),
                property.getLatitude(),
                property.getLongitude(),
                property.getCategory(),
                property.getDealType(),

                property.getDepositAmount(),
                property.getMonthlyRentAmount(),
                property.getMaintenanceFee(),
                formatDepositLabel(property.getDealType(), property.getDepositAmount()),
                formatMonthlyRentLabel(property.getDealType(), property.getMonthlyRentAmount()),

                property.getRoomType(),
                property.getExclusiveAreaM2(),
                property.getSupplyAreaM2(),
                property.getRoomCount(),
                property.getBathroomCount(),

                property.getFloor(),
                property.getTotalFloor(),
                property.getDirection(),
                property.getHeatingType(),
                property.getElevatorAvailable(),
                property.getTotalParkingCount(),
                property.getBuildingUse(),

                property.getMoveInType(),
                property.getMoveInDate(),
                property.getApprovalDate(),
                property.getFirstRegistrationDate(),

                splitText(property.getTag()),
                splitText(property.getOptions()),
                splitText(property.getSecurityFacilities()),

                property.getThumbnailUrl(),
                property.getDescription()
        );
    }

    // 쉼표 문자열을 배열로 변환합니다.
    private static List<String> splitText(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }

        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .toList();
    }

    // 보증금 label을 계산합니다.
    private static String formatDepositLabel(String dealType, Integer amount) {
        if (amount == null) {
            return "보증금 확인 필요";
        }

        if ("jeonse".equals(dealType)) {
            return "전세 " + formatManwon(amount);
        }

        return "보증금 " + formatManwon(amount);
    }

    // 월세 label을 계산합니다.
    private static String formatMonthlyRentLabel(String dealType, Integer amount) {
        if ("jeonse".equals(dealType) || amount == null || amount <= 0) {
            return null;
        }

        return "월세 " + formatManwon(amount);
    }

    // 만원 단위 금액을 화면용으로 변환합니다.
    private static String formatManwon(Integer amount) {
        if (amount >= 10000) {
            int eok = amount / 10000;
            int man = amount % 10000;

            if (man == 0) {
                return eok + "억";
            }

            return eok + "억 " + String.format("%,d", man) + "만";
        }

        return String.format("%,d", amount) + "만";
    }
}