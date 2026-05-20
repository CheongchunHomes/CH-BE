package com.chcorp.homes.policies.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicServiceApiResponse {

    private Integer currentCount;
    private Integer matchCount;
    private Integer page;
    private Integer perPage;
    private Integer totalCount;

    private List<Item> data;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        @JsonProperty("서비스ID")
        private String serviceId;   //정책번호 externalId로 사용

        @JsonProperty("서비스명")
        private String serviceName;          // 서비스명

        @JsonProperty("서비스목적요약")
        private String serviceSummary;       // 서비스 목적 요약

        @JsonProperty("서비스분야")
        private String serviceCategory;      // 서비스 분야

        @JsonProperty("지원내용")
        private String supportContent;       // 지원내용

        @JsonProperty("지원대상")
        private String targetDesc;           // 지원대상

        @JsonProperty("선정기준")
        private String selectionCriteria;    // 선정기준

        @JsonProperty("신청기한")
        private String applyPeriod;          // 신청기한

        @JsonProperty("신청방법")
        private String applyMethod;          // 신청방법

        @JsonProperty("지원유형")
        private String supportType;          // 지원유형

        @JsonProperty("전화문의")
        private String contact;              // 전화문의

        @JsonProperty("접수기관")
        private String receptionOrg;         // 접수기관

        @JsonProperty("소관기관명")
        private String supervisingInstitution;   // 소관기관명

        @JsonProperty("소관기관유형")
        private String institutionType;      // 소관기관유형

        @JsonProperty("소관기관코드")
        private String institutionCode;      // 소관기관코드

        @JsonProperty("상세조회URL")
        private String detailUrl;            // 상세조회 URL

        @JsonProperty("사용자구분")
        private String userType;             // 사용자 구분

        @JsonProperty("등록일시")
        private String createdDate;          // 등록일시

        @JsonProperty("수정일시")
        private String updatedDate;          // 수정일시

        @JsonProperty("조회수")
        private Integer viewCount;           // 조회수
    }
}
