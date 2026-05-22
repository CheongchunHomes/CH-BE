package com.chcorp.homes.policies.dto;

import com.chcorp.homes.policies.entity.Policy;
import lombok.Getter;

@Getter
// 정책 관련 DTO
public class PolicyListDTO {

    private Long policyId;
    private String title;
    private String region;
    private String mainCategory;
    private String subCategory;
    private String originalCategory;
    private String keyword;
    private String summary;
    private String targetDesc;
    private String applyPeriod;
    private String supportType;
    private String supervisingInstitution;
    private String status;

    public PolicyListDTO(Policy entity) {
        this.policyId = entity.getPolicyId();
        this.title = entity.getTitle();
        this.region = entity.getRegion();
        this.mainCategory = entity.getMainCategory();
        this.subCategory = entity.getSubCategory();
        this.originalCategory = entity.getOriginalCategory();
        this.keyword = entity.getKeyword();
        this.summary = entity.getSummary();
        this.targetDesc = entity.getTargetDesc();
        this.applyPeriod = entity.getApplyPeriod();
        this.supportType = entity.getSupportType();
        this.supervisingInstitution = entity.getSupervisingInstitution();
        this.status = entity.getStatus();
    }
}
