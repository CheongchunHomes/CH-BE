package com.chcorp.homes.subscription.entity;

import com.chcorp.homes.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscription_house_types")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubscriptionHouseType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "house_type_id")
    private Long houseTypeId;

    @Column(name = "announcement_id", nullable = false)
    private Long announcementId;

    @Column(name = "house_manage_no")
    private String houseManageNo;

    @Column(name = "pblanc_no")
    private String pblancNo;

    @Column(name = "model_no")
    private String modelNo;

    @Column(name = "house_type_name")
    private String houseTypeName;

    @Column(name = "exclusive_area")
    private String exclusiveArea;

    @Column(name = "supply_household_count")
    private Integer supplyHouseholdCount;

    @Column(name = "special_supply_count")
    private Integer specialSupplyCount;

    @Column(name = "general_supply_count")
    private Integer generalSupplyCount;

    @Column(name = "supply_price")
    private Long supplyPrice;

    @Column(name = "rent_deposit")
    private Long rentDeposit;

    @Column(name = "monthly_rent")
    private Long monthlyRent;
}