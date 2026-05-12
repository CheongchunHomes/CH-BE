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

    // 매물 기본 정보
    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(length = 50)
    private String region;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(length = 50)
    private String category;

    @Column(name = "deal_type", length = 30)
    private String dealType;

    // 금액 정보
    @Column(name = "deposit_amount")
    private Integer depositAmount;

    @Column(name = "monthly_rent_amount")
    private Integer monthlyRentAmount;

    @Column(name = "maintenance_fee")
    private Integer maintenanceFee;


    // 구조 정보
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

    // 건물 정보
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

    // 입주 및 등록 정보
    @Column(name = "move_in_type", length = 50)
    private String moveInType;

    @Column(name = "move_in_date")
    private LocalDate moveInDate;

    @Column(name = "approval_date")
    private LocalDate approvalDate;

    @Column(name = "first_registration_date")
    private LocalDate firstRegistrationDate;

    // 쉼표 문자열 저장 정보
    @Column(columnDefinition = "TEXT")
    private String tag;

    @Column(columnDefinition = "TEXT")
    private String options;

    @Column(name = "security_facilities", columnDefinition = "TEXT")
    private String securityFacilities;

    // 화면 표시 정보
    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(columnDefinition = "TEXT")
    private String description;
}