package com.chcorp.homes.policies.dto;

import com.chcorp.homes.policies.entity.Policy;
import lombok.Getter;

@Getter
public class PolicyDetailDTO {

    private Long policyId;

    private String externalId;
    private String sourceType;

    private String title;
    private String region;
    private String mainCategory;
    private String subCategory;
    private String originalCategory;
    private String originalMiddleCategory;
    private String keyword;

    private String summary;
    private String content;
    private String targetDesc;
    private String excludedTarget;
    private String selectionCriteria;

    private String applyMethod;
    private String screeningMethod;
    private String applyPeriod;
    private String businessPeriod;
    private String requiredDocuments;
    private String etc;

    private String supervisingInstitution;
    private String operatingInstitution;
    private String receptionOrg;
    private String contact;
    private String sourceUrl;
    private String onlineApplyUrl;
    private String law;
    private String supportType;

    private Integer ageMin;
    private Integer ageMax;
    private Long incomeMin;
    private Long incomeMax;
    private String incomeDesc;
    private Boolean homelessRequired;

    private Boolean isVisible;
    private String status;

    public PolicyDetailDTO(Policy entity) {
        this.policyId = entity.getPolicyId();

        this.externalId = entity.getExternalId();
        this.sourceType = entity.getSourceType();

        this.title = entity.getTitle();
        this.region = entity.getRegion();
        this.mainCategory = entity.getMainCategory();
        this.subCategory = entity.getSubCategory();
        this.originalCategory = entity.getOriginalCategory();
        this.originalMiddleCategory = entity.getOriginalMiddleCategory();
        this.keyword = entity.getKeyword();

        this.summary = entity.getSummary();
        this.content = entity.getContent();
        this.targetDesc = entity.getTargetDesc();
        this.excludedTarget = entity.getExcludedTarget();
        this.selectionCriteria = entity.getSelectionCriteria();

        this.applyMethod = entity.getApplyMethod();
        this.screeningMethod = entity.getScreeningMethod();
        this.applyPeriod = entity.getApplyPeriod();
        this.businessPeriod = entity.getBusinessPeriod();
        this.requiredDocuments = entity.getRequiredDocuments();
        this.etc = entity.getEtc();

        this.supervisingInstitution = entity.getSupervisingInstitution();
        this.operatingInstitution = entity.getOperatingInstitution();
        this.receptionOrg = entity.getReceptionOrg();
        this.contact = entity.getContact();
        this.sourceUrl = entity.getSourceUrl();
        this.onlineApplyUrl = entity.getOnlineApplyUrl();
        this.law = entity.getLaw();
        this.supportType = entity.getSupportType();

        this.ageMin = entity.getAgeMin();
        this.ageMax = entity.getAgeMax();
        this.incomeMin = entity.getIncomeMin();
        this.incomeMax = entity.getIncomeMax();
        this.incomeDesc = entity.getIncomeDesc();
        this.homelessRequired = entity.getHomelessRequired();

        this.isVisible = entity.getIsVisible();
        this.status = entity.getStatus();
    }
}
