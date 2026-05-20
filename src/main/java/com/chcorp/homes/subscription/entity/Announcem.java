package com.chcorp.homes.subscription.entity;

import com.chcorp.homes.common.entity.MutableBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "announcements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Announcem extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "announcement_id")
    private Long announcementId;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "pblanc_no")
    private String pblancNo;

    @Column(name = "source_type", nullable = false)
    private String sourceType;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "region")
    private String region;

    // 기존 컬럼 유지용
    @Column(name = "recruitment_type")
    private String recruitmentType;

    // 실제 현재 프로젝트에서 사용하는 컬럼
    @Column(name = "recuitment_type")
    private String recuitmentType;

    @Column(name = "target_type")
    private String targetType;

    @Column(name = "requires_sub_account")
    private Boolean requiresSubAccount;

    @Column(name = "income_condition")
    private String incomeCondition;

    @Column(name = "special_supply_yn")
    private Boolean specialSupplyYn;

    @Column(name = "content")
    private String content;

    @Column(name = "apply_start_date")
    private LocalDate applyStartDate;

    @Column(name = "apply_end_date")
    private LocalDate applyEndDate;

    @Column(name = "status")
    private String status;

    @Column(name = "is_visible")
    private Boolean isVisible;

    @Column(name = "source_url")
    private String sourceUrl;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "supply_institution")
    private String supplyInstitution;

    @Column(name = "tot_hshld_co")
    private String totHshldCo;

    @Column(name = "rent_gtn")
    private Integer rentGtn;

    @Column(name = "mt_rntchrg")
    private Integer mtRntchrg;

    @Column(name = "heat_mthd_nm")
    private String heatMthdNm;

    @Column(name = "begin_de")
    private LocalDate beginDe;

    @Column(name = "end_de")
    private LocalDate endDe;

    @Column(name = "cntrct_cncls_bgnde")
    private LocalDate cntrctCnclsBgnde;

    @Column(name = "cntrct_cncls_endde")
    private LocalDate cntrctCnclsEndde;

    @Column(name = "mvn_prearnge_ym")
    private String mvnPrearngeYm;

    @Column(name = "przwner_presnatn_de")
    private LocalDate przwnerPresnatnDe;
}