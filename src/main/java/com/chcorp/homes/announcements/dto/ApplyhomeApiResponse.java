package com.chcorp.homes.announcements.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplyhomeApiResponse {

    @JsonProperty("data")
    private List<Item> data;

    @JsonProperty("totalCount")
    private int totalCount;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {

        @JsonProperty("HOUSE_NM")
        private String houseNm;

        @JsonProperty("SUBSCRPT_AREA_CODE_NM")
        private String subscrptAreaCodeNm;

        @JsonProperty("HSSPLY_ADRES")
        private String hssplyAdres;

        @JsonProperty("HOUSE_SECD_NM")
        private String houseSecdNm;

        @JsonProperty("PBLANC_URL")
        private String pblancUrl;

        @JsonProperty("PBLANC_NO")
        private String pblancNo;

        @JsonProperty("RCRIT_PBLANC_DE")
        private String rcritPblancDe;

        @JsonProperty("SUBSCRPT_RCEPT_BGNDE")
        private String subscrptRceptBgnde;

        @JsonProperty("SUBSCRPT_RCEPT_ENDDE")
        private String subscrptRceptEndde;

        @JsonProperty("PRZWNER_PRESNATN_DE")
        private String przwnerPresnatnDe;

        @JsonProperty("BSNS_MBY_NM")
        private String bsnsMbyNm;

        @JsonProperty("TOT_SUPLY_HSHLDCO")
        private Integer totSuplyHshldco;

        @JsonProperty("CNTRCT_CNCLS_BGNDE")
        private String cntrctCnclsBgnde;

        @JsonProperty("CNTRCT_CNCLS_ENDDE")
        private String cntrctCnclsEndde;
    }


}
