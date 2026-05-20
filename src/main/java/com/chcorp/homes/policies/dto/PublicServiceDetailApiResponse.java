package com.chcorp.homes.policies.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicServiceDetailApiResponse {

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
        private String serviceId;            // 서비스 고유 ID

        @JsonProperty("서비스명")
        private String serviceName;          // 서비스명

        @JsonProperty("서비스목적")
        private String servicePurpose;       // 서비스 목적 상세

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

        @JsonProperty("구비서류")
        private String requiredDocuments;    // 구비서류

        @JsonProperty("공무원확인구비서류")
        private String officerCheckDocuments; // 공무원 확인 구비서류

        @JsonProperty("본인확인필요구비서류")
        private String identityCheckDocuments; // 본인 확인 필요 구비서류

        @JsonProperty("문의처")
        private String contact;              // 문의처

        @JsonProperty("법령")
        private String law;                  // 관련 법령

        @JsonProperty("행정규칙")
        private String administrativeRule;   // 행정규칙

        @JsonProperty("자치법규")
        private String localRegulation;      // 자치법규

        @JsonProperty("소관기관명")
        private String supervisingInstitution; // 소관기관명

        @JsonProperty("접수기관명")
        private String receptionOrg;         // 접수기관명

        @JsonProperty("온라인신청사이트URL")
        private String onlineApplyUrl;       // 온라인 신청 사이트 URL

        @JsonProperty("지원유형")
        private String supportType;          // 지원유형

        @JsonProperty("수정일시")
        private String updatedDate;          // 수정일시
    }
}