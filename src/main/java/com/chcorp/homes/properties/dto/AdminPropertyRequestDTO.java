package com.chcorp.homes.properties.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AdminPropertyRequestDTO {

    private String title;
    private String address;
    private String region;
    private Long landlordUserId;
    private String category;
    private String dealType;
    private Integer depositAmount;
    private Integer monthlyRentAmount;
    private Integer maintenanceFee;
    private String roomType;
    private BigDecimal exclusiveAreaM2;
    private BigDecimal supplyAreaM2;
    private Integer roomCount;
    private Integer bathroomCount;
    private Integer floor;
    private Integer totalFloor;
    private String direction;
    private String heatingType;
    private Boolean elevatorAvailable;
    private Integer totalParkingCount;
    private String buildingUse;
    private String moveInType;
    private LocalDate moveInDate;
    private LocalDate approvalDate;
    private LocalDate firstRegistrationDate;
    private String tag;
    private String options;
    private String securityFacilities;
    private String thumbnailUrl;
    private String description;
}
