package com.chcorp.homes.policies.dto;

import com.chcorp.homes.policies.entity.Policy;
import com.chcorp.homes.policies.entity.PolicyScrap;
import lombok.Getter;

import java.time.Instant;

@Getter
public class PolicyScrapDTO {

    private Long scrapId;
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

    private Instant scrapedAt;

    private Boolean policyVisible;

    public PolicyScrapDTO (PolicyScrap scrap) {
        Policy policy = scrap.getPolicy();

        this.scrapId = scrap.getScrapId();
        this.policyId = policy.getPolicyId();
        this.title = policy.getTitle();
        this.region = policy.getRegion();
        this.mainCategory = policy.getMainCategory();
        this.subCategory = policy.getSubCategory();
        this.originalCategory = policy.getOriginalCategory();
        this.keyword = policy.getKeyword();
        this.summary = policy.getSummary();
        this.targetDesc = policy.getTargetDesc();
        this.applyPeriod = policy.getApplyPeriod();
        this.supportType = policy.getSupportType();
        this.supervisingInstitution = policy.getSupervisingInstitution();
        this.status = policy.getStatus();

        this.scrapedAt = scrap.getCreatedAt();

        this.policyVisible = policy.getIsVisible();


    }
}
