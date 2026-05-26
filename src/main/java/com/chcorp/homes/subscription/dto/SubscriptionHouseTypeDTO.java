package com.chcorp.homes.subscription.dto;

import com.chcorp.homes.subscription.entity.SubscriptionHouseType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubscriptionHouseTypeDTO {

    private Long houseTypeId;
    private Long announcementId;
    private String houseManageNo;
    private String pblancNo;
    private String modelNo;
    private String houseTypeName;
    private String exclusiveArea;
    private Integer supplyHouseholdCount;
    private Integer specialSupplyCount;
    private Integer generalSupplyCount;
    private Long supplyPrice;
    private Long rentDeposit;
    private Long monthlyRent;

    public static SubscriptionHouseTypeDTO from(SubscriptionHouseType houseType) {
        return SubscriptionHouseTypeDTO.builder()
                .houseTypeId(houseType.getHouseTypeId())
                .announcementId(houseType.getAnnouncementId())
                .houseManageNo(houseType.getHouseManageNo())
                .pblancNo(houseType.getPblancNo())
                .modelNo(houseType.getModelNo())
                .houseTypeName(houseType.getHouseTypeName())
                .exclusiveArea(houseType.getExclusiveArea())
                .supplyHouseholdCount(houseType.getSupplyHouseholdCount())
                .specialSupplyCount(houseType.getSpecialSupplyCount())
                .generalSupplyCount(houseType.getGeneralSupplyCount())
                .supplyPrice(houseType.getSupplyPrice())
                .rentDeposit(houseType.getRentDeposit())
                .monthlyRent(houseType.getMonthlyRent())
                .build();
    }

    public static SubscriptionHouseTypeDTO defaultForMyhome(Long announcementId) {
        return SubscriptionHouseTypeDTO.builder()
                .houseTypeId(null)
                .announcementId(announcementId)
                .houseManageNo(null)
                .pblancNo(null)
                .modelNo(null)
                .houseTypeName("기본타입")
                .exclusiveArea("미정")
                .supplyHouseholdCount(null)
                .specialSupplyCount(null)
                .generalSupplyCount(null)
                .supplyPrice(null)
                .rentDeposit(null)
                .monthlyRent(null)
                .build();
    }
}
