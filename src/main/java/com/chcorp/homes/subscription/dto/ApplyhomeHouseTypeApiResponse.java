package com.chcorp.homes.subscription.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplyhomeHouseTypeApiResponse {

    @JsonProperty("data")
    private List<Item> data;

    @JsonProperty("totalCount")
    private int totalCount;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {

        @JsonProperty("HOUSE_MANAGE_NO")
        @JsonAlias({"HOUSE_MANAGE_NO", "HOUSE_MANAGE_NO"})
        private String houseManageNo;

        @JsonProperty("PBLANC_NO")
        @JsonAlias({"PBLANC_NO"})
        private String pblancNo;

        @JsonProperty("MODEL_NO")
        @JsonAlias({"MODEL_NO"})
        private String modelNo;

        @JsonProperty("HOUSE_TY")
        @JsonAlias({"HOUSE_TY", "HOUSE_TYPE", "HOUSETY"})
        private String houseTypeName;

        @JsonProperty("SUPLY_AR")
        @JsonAlias({"SUPLY_AR", "HSSPLY_AR", "EXCLUSIVE_AREA"})
        private String exclusiveArea;

        @JsonProperty("SUPLY_HSHLDCO")
        @JsonAlias({"SUPLY_HSHLDCO", "SUPPLY_HOUSEHOLD_COUNT"})
        private String supplyHouseholdCount;

        @JsonProperty("SPSPLY_HSHLDCO")
        @JsonAlias({"SPSPLY_HSHLDCO", "SPECIAL_SUPPLY_COUNT"})
        private String specialSupplyCount;

        @JsonProperty("GNRL_SUPLY_HSHLDCO")
        @JsonAlias({"GNRL_SUPLY_HSHLDCO", "GENERAL_SUPPLY_COUNT"})
        private String generalSupplyCount;

        @JsonProperty("LTTOT_TOP_AMOUNT")
        @JsonAlias({"LTTOT_TOP_AMOUNT", "SUPLY_AMOUNT", "SUPPLY_PRICE"})
        private String supplyPrice;
    }
}