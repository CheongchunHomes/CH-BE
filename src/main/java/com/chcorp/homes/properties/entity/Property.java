package com.chcorp.homes.properties.entity;

import com.chcorp.homes.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "properties")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Property extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "property_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(length = 50)
    private String region;

    @Column(name = "landlord_user_id")
    private Long landlordUserId;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(length = 50)
    private String category;

    @Column(name = "deal_type", length = 30)
    private String dealType;

    @Column(name = "deposit_amount")
    private Integer depositAmount;

    @Column(name = "monthly_rent_amount")
    private Integer monthlyRentAmount;

    @Column(name = "maintenance_fee")
    private Integer maintenanceFee;

    @Column(name = "room_type", length = 50)
    private String roomType;

    @Column(name = "exclusive_area_m2", precision = 8, scale = 2)
    private BigDecimal exclusiveAreaM2;

    @Column(name = "supply_area_m2", precision = 8, scale = 2)
    private BigDecimal supplyAreaM2;

    @Column(name = "room_count")
    private Integer roomCount;

    @Column(name = "bathroom_count")
    private Integer bathroomCount;

    @Column
    private Integer floor;

    @Column(name = "total_floor")
    private Integer totalFloor;

    @Column(length = 30)
    private String direction;

    @Column(name = "heating_type", length = 50)
    private String heatingType;

    @Column(name = "elevator_available")
    private Boolean elevatorAvailable;

    @Column(name = "total_parking_count")
    private Integer totalParkingCount;

    @Column(name = "building_use", length = 100)
    private String buildingUse;

    @Column(name = "move_in_type", length = 50)
    private String moveInType;

    @Column(name = "move_in_date")
    private LocalDate moveInDate;

    @Column(name = "approval_date")
    private LocalDate approvalDate;

    @Column(name = "first_registration_date")
    private LocalDate firstRegistrationDate;

    @Column(columnDefinition = "TEXT")
    private String tag;

    @Column(columnDefinition = "TEXT")
    private String options;

    @Column(name = "security_facilities", columnDefinition = "TEXT")
    private String securityFacilities;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    public void updateAdminFields(
            String title,
            String address,
            String region,
            Long landlordUserId,
            Double latitude,
            Double longitude,
            String category,
            String dealType,
            Integer depositAmount,
            Integer monthlyRentAmount,
            Integer maintenanceFee,
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
            String tag,
            String options,
            String securityFacilities,
            String thumbnailUrl,
            String description
    ) {
        this.title = title;
        this.address = address;
        this.region = region;
        this.landlordUserId = landlordUserId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
        this.dealType = dealType;
        this.depositAmount = depositAmount;
        this.monthlyRentAmount = monthlyRentAmount;
        this.maintenanceFee = maintenanceFee;
        this.roomType = roomType;
        this.exclusiveAreaM2 = exclusiveAreaM2;
        this.supplyAreaM2 = supplyAreaM2;
        this.roomCount = roomCount;
        this.bathroomCount = bathroomCount;
        this.floor = floor;
        this.totalFloor = totalFloor;
        this.direction = direction;
        this.heatingType = heatingType;
        this.elevatorAvailable = elevatorAvailable;
        this.totalParkingCount = totalParkingCount;
        this.buildingUse = buildingUse;
        this.moveInType = moveInType;
        this.moveInDate = moveInDate;
        this.approvalDate = approvalDate;
        this.firstRegistrationDate = firstRegistrationDate;
        this.tag = tag;
        this.options = options;
        this.securityFacilities = securityFacilities;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
    }
}