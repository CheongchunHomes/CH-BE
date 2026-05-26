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
public class ApplyhomeAptAnnouncementApiResponse {

    @JsonProperty("data")
    private List<Item> data;

    @JsonProperty("totalCount")
    private int totalCount;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {

        @JsonProperty("HOUSE_MANAGE_NO")
        @JsonAlias({"HOUSE_MANAGE_NO"})
        private String houseManageNo;

        @JsonProperty("PBLANC_NO")
        @JsonAlias({"PBLANC_NO"})
        private String pblancNo;

        @JsonProperty("HOUSE_NM")
        @JsonAlias({"HOUSE_NM", "HOUSE_NAME"})
        private String houseName;

        @JsonProperty("HOUSE_SECD_NM")
        @JsonAlias({"HOUSE_SECD_NM"})
        private String houseSecdName;

        @JsonProperty("HOUSE_DTL_SECD_NM")
        @JsonAlias({"HOUSE_DTL_SECD_NM"})
        private String houseDtlSecdName;

        @JsonProperty("RENT_SECD_NM")
        @JsonAlias({"RENT_SECD_NM"})
        private String rentSecdName;

        @JsonProperty("SUBSCRPT_AREA_CODE_NM")
        @JsonAlias({"SUBSCRPT_AREA_CODE_NM", "SUBSCRPT_AREA_NM"})
        private String region;

        @JsonProperty("HSSPLY_ADRES")
        @JsonAlias({"HSSPLY_ADRES", "SUPPLY_ADDRESS"})
        private String address;

        @JsonProperty("TOT_SUPLY_HSHLDCO")
        @JsonAlias({"TOT_SUPLY_HSHLDCO", "TOT_SUPLY_HSHLD_CO"})
        private String totalHouseholdCount;

        @JsonProperty("RCRIT_PBLANC_DE")
        @JsonAlias({"RCRIT_PBLANC_DE", "RECRUIT_NOTICE_DATE"})
        private String noticeDate;

        @JsonProperty("RCEPT_BGNDE")
        @JsonAlias({"RCEPT_BGNDE", "RECEIPT_START_DATE"})
        private String applyStartDate;

        @JsonProperty("RCEPT_ENDDE")
        @JsonAlias({"RCEPT_ENDDE", "RECEIPT_END_DATE"})
        private String applyEndDate;

        @JsonProperty("PRZWNER_PRESNATN_DE")
        @JsonAlias({"PRZWNER_PRESNATN_DE", "WINNER_PRESENTATION_DATE"})
        private String winnerDate;

        @JsonProperty("CNTRCT_CNCLS_BGNDE")
        @JsonAlias({"CNTRCT_CNCLS_BGNDE"})
        private String contractStartDate;

        @JsonProperty("CNTRCT_CNCLS_ENDDE")
        @JsonAlias({"CNTRCT_CNCLS_ENDDE"})
        private String contractEndDate;

        @JsonProperty("MVN_PREARNGE_YM")
        @JsonAlias({"MVN_PREARNGE_YM"})
        private String moveInExpectedYm;

        @JsonProperty("HMPG_ADRES")
        @JsonAlias({"HMPG_ADRES", "HOMEPAGE_URL"})
        private String homepageUrl;

        @JsonProperty("BSNS_MBY_NM")
        @JsonAlias({"BSNS_MBY_NM", "SUPPLY_INSTITUTION"})
        private String supplyInstitution;
    }
}