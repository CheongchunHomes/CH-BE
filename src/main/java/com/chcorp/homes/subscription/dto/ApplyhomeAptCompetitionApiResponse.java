package com.chcorp.homes.subscription.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplyhomeAptCompetitionApiResponse {

    @JsonProperty("page")
    private int page;

    @JsonProperty("perPage")
    private int perPage;

    @JsonProperty("totalCount")
    private int totalCount;

    @JsonProperty("currentCount")
    private int currentCount;

    @JsonProperty("matchCount")
    private int matchCount;

    @JsonProperty("data")
    private List<Item> data;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {

        @JsonProperty("주택관리번호")
        private String houseManageNo;

        @JsonProperty("공고번호")
        private String pblancNo;

        @JsonProperty("모델번호")
        private String modelNo;

        @JsonProperty("주택형")
        private String houseTypeName;

        @JsonProperty("공급세대수")
        private Integer supplyCount;

        @JsonProperty("순위")
        private Integer rankNo;

        @JsonProperty("거주코드")
        private Integer residenceCode;

        @JsonProperty("거주지역")
        private String residenceArea;

        @JsonProperty("접수건수")
        private Integer requestCount;

        @JsonProperty("경쟁률")
        private String competitionRate;
    }
}