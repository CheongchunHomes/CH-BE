package com.chcorp.homes.policies.entity;

import com.chcorp.homes.common.entity.MutableBaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table (
        name = "policies",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_policy_source_external",
                        columnNames = {"source", "external_id"}
                )
        }
)
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Policy extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Long policyId;

    // ================
    // 외부 API 식별 정보
    // ================

    // 청년정책 API: plcyNo
    // 공공서비스 API: 서비스ID
    // 관리자 직접 등록: null 가능
    @Column(name = "external_id")
    private String externalId;

    // 청년정책 api, 공공서비스 api, 관리자 직접 등록 등 정책 출처 구분
    @Column(name = "source_type")
    private String sourceType;

    // ================
    // 기본 정보
    // ================

    // 정책명 / 서비스명
    @Column(nullable = false)
    private String title;

    // 지역명 또는 기관명에서 추출한 지역
    @Column
    private String region;

    // 서비스 기준 대분류 예) 주거비 지원, 기타지원, 기타사업
    @Column(name = "main_category")
    private String mainCategory;

    // 서비스 기준 소분류 예) 월세지원, 보증금지원, 주거급여 등
    @Column(name = "sub_category")
    private String subCategory;

    // 원본 API 대분류 (청년정책 API: mclsfNm, 공공서비스 API에 없으면 null)
    @Column(name = "original_category")
    private String originalCategory;

    // 원본 API 중분류 (청년정책 API: mclsfNm, 공공서비스 API에 없으면 null)
    @Column(name = "original_middle_category")
    private String originalMiddleCategory;

    // 정책 키워드 (청년정책 API: plcyKywdNm, 공공서비스 API에 없으면 null)
    @Column
    private String keyword;

    // 정책 요약 (청년정책 API: plcyExplnCn, 공공서비스 API: 서비스목적요약 또는 서비스목적)
    @Column(columnDefinition = "TEXT")
    private String summary;

    // 지원 내용 (청년정책 API: plcySprtCn, 공공서비스 API: 지원내용)
    @Column(columnDefinition = "TEXT")
    private String content;

    // 지원 대상 또는 추가 자격 조건 (청년정책 API: addAplyQlfcCndCn, 공공서비스 API: 지원대상)
    @Column(name = "target_desc", columnDefinition = "TEXT")
    private String targetDesc;

    // 참여 제한 대상 또는 제외 대상 (청년정책 API: ptcpPrpTrgtCn, 공공서비스 API에는 별도 필드가 없으면 null)
    @Column(name = "excluded_target", columnDefinition = "TEXT")
    private String excludedTarget;

    // 선정기준 (공공서비스 API: 선정기준, 청년정책 API에는 별도 필드가 없으면 null)
    @Column(name = "selection_criteria", columnDefinition = "TEXT")
    private String selectionCriteria;

    // ===================
    // 신청 정보
    // ===================

    // 신청방법
    @Column(name = "apply_method", columnDefinition = "TEXT")
    private String applyMethod;

    //심사방법
    @Column(name = "screening_method", columnDefinition = "TEXT")
    private String screeningMethod;

    // 신청기간
    @Column(name = "apply_period")
    private String applyPeriod;

    // 사업기간
    @Column(name = "business_period")
    private String businessPeriod;

    // 구비서류 또는 제출서류
    @Column(name = "required_documents", columnDefinition = "TEXT")
    private String requiredDocuments;

    // 기타사항
    @Column(columnDefinition = "TEXT")
    private String etc;

    // ===================
    // 기관 / 문의 / 링크
    // ===================

    // 주관기관 또는 소관기관
    @Column(name = "supervising_institution")
    private String supervisingInstitution;

    // 운영기관
    @Column(name = "operating_institution")
    private String operatingInstitution;

    // 접수기관
    @Column(name = "reception_org")
    private String receptionOrg;

    // 문의처
    @Column(columnDefinition = "TEXT")
    private String contact;

    // 원문 상세 url
    @Column(name = "source_url", columnDefinition = "TEXT")
    private String sourceUrl;

    // 온라인 신청 url
    @Column(name = "online_apply_url", columnDefinition = "TEXT")
    private String onlineApplyUrl;

    // 관련 법령
    @Column(columnDefinition = "TEXT")
    private String law;

    // 지원유형
    @Column(name = "support_type")
    private String supportType;

    // =========================
    // 조건 정보
    // =========================

    // 최소 나이
    @Column(name = "age_min")
    private Integer ageMin;

    // 최대 나이
    @Column(name = "age_max")
    private Integer ageMax;

    // 최소 소득
    @Column(name = "income_min")
    private Long incomeMin;

    // 최대 소득
    @Column(name = "income_max")
    private Long incomeMax;

    // 소득 조건 설명
    @Column(name = "income_desc", columnDefinition = "TEXT")
    private String incomeDesc;

    // 무주택 필요 여부
    @Column(name = "homeless_required")
    private Boolean homelessRequired;

    // =========================
    // 노출 / 상태
    // =========================

    // 사용자 화면 노출 여부
    @Column(name = "is_visible")
    private Boolean isVisible;

    // 정책 상태
    @Column
    private String status;
    // 예: "상시신청", "신청가능", "마감", "확인필요"

    // =======================
    // 관리자 도메인 메서드
    // =======================
    public void updateVisibility(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public void updateAdminFields(String title, String mainCategory,
                                  String subCategory, String status,
                                  String content, String sourceUrl,
                                  String onlineApplyUrl, String supervisingInstitution,
                                  String summary, String targetDesc,
                                  String applyMethod, String requiredDocuments,
                                  String applyPeriod, String supportType,
                                  Boolean isVisible) {
        this.title = title;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.status = status;
        this.content = content;
        this.sourceUrl = sourceUrl;
        this.onlineApplyUrl = onlineApplyUrl;
        this.supervisingInstitution = supervisingInstitution;
        this.summary = summary;
        this.targetDesc = targetDesc;
        this.applyMethod = applyMethod;
        this.requiredDocuments = requiredDocuments;
        this.applyPeriod = applyPeriod;
        this.supportType = supportType;
        this.isVisible = isVisible;
    }
}
