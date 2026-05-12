package com.chcorp.homes.announcements.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RentalHouseApiResponse {

    private Response response;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private Body body;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        private String numOfRows;
        private String pageNo;

        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<Item> item;

        private String totalCount;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private String suplyHoCo;
        private String pblancId;
        private Integer houseSn;
        private String sttusNm;
        private String pblancNm;
        private String suplyInsttNm;
        private String houseTyNm;
        private String suplyTyNm;
        private String beforePblancId;
        private String rcritPblancDe;
        private String przwnerPresnatnDe;
        private String refrnc;
        private String url;
        private String pcUrl;
        private String mobileUrl;
        private String hsmpNm;
        private String brtcNm;
        private String signguNm;
        private String fullAdres;
        private String rnCodeNm;
        private String refrnLegaldongNm;
        private String pnu;
        private String heatMthdNm;
        private String totHshldCo;
        private Integer sumSuplyCo;
        private Integer rentGtn;
        private Integer enty;
        private Integer prtpay;
        private Integer surlus;
        private Integer mtRntchrg;
        private String beginDe;
        private String endDe;
    }
}