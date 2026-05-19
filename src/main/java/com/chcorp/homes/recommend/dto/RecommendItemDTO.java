package com.chcorp.homes.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendItemDTO {

    private String name;        // 제도, 대출명
    private String category;    // 카테고리
    private String description; // 간단 설명
    private int matchScore;     // 매칭 점수(높을수록 앞에 노출)
    private String applyUrl;    // 신청 링크
    private int minAge;
    private int maxAge;
    private int maxIncome;
    private Long announcementId;

}
