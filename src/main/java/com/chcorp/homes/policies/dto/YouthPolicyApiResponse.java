package com.chcorp.homes.policies.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class YouthPolicyApiResponse {

    private Integer resultCode;
    private String resultMessage;
    private Result result;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        private Pagging pagging;
        private List<Item> youthPolicyList;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Pagging {
        private Integer totCount;   // 전체 데이터 수
        private Integer pageNum;    // 현재 페이지 번호
        private Integer pageSize;   // 페이지 크기
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        // 기본 식별 정보
        private String plcyNo;  //정책번호 externalId로 사용
        private String plcyNm;  //정책명
        private String plcyKywdNm;  //정책키워드명
        private String plcyExplnCn;  //정책설명내용

        // 원본 분류 정보
        private String lclsfNm; // 대분류
        private String mclsfNm; // 중분류

        // 지원 내용
        private String plcySprtCn; // 지원내용
        private String addAplyQlfcCndCn;    // 추가 신청 자격 조건
        private String ptcpPrpTrgtCn;   // 참여 제한 대상

        // 기관 정보
        private String sprvsnInstCdNm;  // 주관기관명
        private String operInstCdNm;    // 운영기관명
        private String rgtrUpInstCdNm;  // 등록/수정 기관명
        private String rgtrHghrkInstCdNm;   // 등록/수정 상위기관명

        // 신청 / 사업 기간
        private String aplyYmd; // 신청기간
        private String bizPrdBgngYmd;   // 사업기간 시작
        private String bizPrdEndYmd;    // 사업기간 종료
        private String bizPrdEtcCn;     // 사업기간 기타 설명

        // 신청 방법 / 제출 서류
        private String plcyAplyMthdCn;      // 신청방법
        private String srngMthdCn;          // 심사방법
        private String sbmsnDcmntCn;        // 제출서류
        private String etcMttrCn;           // 기타사항
        private String aplyUrlAddr;         // 신청 URL

        // 대상조건
        private String sprtTrgtMinAge;      // 최소 나이
        private String sprtTrgtMaxAge;      // 최대 나이
        private String sprtTrgtAgeLmtYn;    // 나이 제한 여부

        private String earnMinAmt;          // 최소 소득
        private String earnMaxAmt;          // 최대 소득
        private String earnEtcCn;           // 소득 기타조건

        // 원문 / 메타 정보
        private String refUrlAddr1;         // 원문 URL 1
        private String refUrlAddr2;         // 원문 URL 2
        private String frstRegDt;           // 최초 등록일
        private String lastMdfcnDt;         // 최종 수정일
    }

}
