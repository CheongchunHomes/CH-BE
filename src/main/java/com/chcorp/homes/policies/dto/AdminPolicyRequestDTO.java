package com.chcorp.homes.policies.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminPolicyRequestDTO {

    private String title;
    private String region;
    private String mainCategory;
    private String subCategory;
    private String status;
    private String applyPeriod;
    private String supportType;
    private String supervisingInstitution;
    private String summary;
    private String targetDesc;
    private String content;
    private String applyMethod;
    private String requiredDocuments;
    private String sourceUrl;
    private String onlineApplyUrl;
    private Boolean isVisible;
}
