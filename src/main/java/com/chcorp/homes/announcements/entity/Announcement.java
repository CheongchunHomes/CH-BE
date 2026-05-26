package com.chcorp.homes.announcements.entity;

import com.chcorp.homes.common.entity.MutableBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

// api 호출 시 반환되는 객체
@Entity
@Table(name = "announcements")
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Announcement extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "announcement_id")
    private Long announcementId;

    @Column(name = "external_id")
    private String externalId;      // API의 pblanId


    @Column(name = "pblanc_no")
    private String pblancNo;         // 청약홈 공고번호

    @Column(name = "source_type")
    private String sourceType;      // "LH", "청약홈" 등 출처 구분

    @Column
    private String title;       // 공고명

    @Column
    private String region;    // 지역 정보 (예: "서울", "경기", "인천")

    @Column(name = "recuitment_type")
    private String recuitmentType;  // 모집 유형 (예: "공고", "특별공급")

    @Column(name = "target_type")
    private String targetType;      // 대상

    @Column(name = "requires_sub_account")
    private Boolean requiresSubAccount;     // 청약통장 필요 여부

    @Column(name = "income_condition")
    private String incomeCondition;    // 소득 조건 (예: "100% 이하", "150% 이하")

    @Column(name = "special_supply_yn")
    private Boolean specialSupplyYn;    // 특별공급 여부

    @Column(columnDefinition = "TEXT")
    private String content;     // 공고 내용 (예: 모집 공고문, 상세 정보 등)

    @Column(name = "apply_start_date")
    private LocalDate applyStartDate;    // 신청 시작일

    @Column(name = "apply_end_date")
    private LocalDate applyEndDate;     // 신청 종료일

    @Column
    private String status;      // 공고 상태 (예: "모집중", "마감", "예정")

    @Column(name = "is_visible")
    private Boolean isVisible = true;   // 공고의 노출 여부 (true: 노출, false: 숨김)

    @Column(name = "source_url", columnDefinition = "TEXT")
    private String sourceUrl;   // 공고의 원본 URL (예: LH 청약홈의 공고 페이지 URL)

    @Column(columnDefinition = "TEXT")
    private String address;     // 공고의 주소 정보 (예: "서울특별시 강남구 역삼동 123-45")

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;    // 공고 위치의 위도 정보

    // 추가
    @Column(name = "supply_institution")
    private String supplyInstitution;    // 공급 기관 (예: "LH", "SH", "서울주택도시공사" 등)

    @Column(name = "tot_hshld_co")
    private String totHshldCo;  // 총 가구 수 (예: "100세대")

    @Column(name = "rent_gtn")
    private Integer rentGtn;       // 임대보증금

    @Column(name = "mt_rntchrg")
    private Integer mtRntchrg;     // 월 임대료

    @Column(name = "heat_mthd_nm")
    private String heatMthdNm;  // 난방 방식 (예: "개별난방", "중앙난방" 등)

    @Column(name = "begin_de")
    private LocalDate beginDe;      // 공고 시작일 (예: "2024-04-01")

    @Column(name = "end_de")
    private LocalDate endDe;        // 공고 종료일 (예: "2024-04-30")

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;   // 공고 위치의 경도 정보

    @Column(name = "cntrct_cncls_bgnde")
    private LocalDate cntrctCnclsBgnde;     // 계약 시작일

    @Column(name = "cntrct_cncls_endde")
    private LocalDate cntrctCnclsEndde;     // 계약 종료일

    @Column(name = "mvn_prearnge_ym")
    private String mvnPrearngeYm;           // 입주 예정월 (예: "202606")

    @Column(name = "przwner_presnatn_de")
    private LocalDate przwnerPresnatnDe;    // 당첨자 발표일

    @Column(name = "surlus")
    private Integer surlus;


    // =======================
    // 관리자 도메인 메서드
    // =======================

    public void updateCoordinate(Double latitude, Double longitude) {
        this.latitude = latitude == null ? null : BigDecimal.valueOf(latitude);
        this.longitude = longitude == null ? null : BigDecimal.valueOf(longitude);
    }

    public void updateAdminFields(String title, String region,
                                  String recuitmentType, String targetType,
                                  String status, String address,
                                  String content, String sourceUrl,
                                  Boolean isVisible, LocalDate applyStartDate, LocalDate applyEndDate) {
        this.title = title;
        this.region = region;
        this.recuitmentType = recuitmentType;
        this.targetType = targetType;
        this.status = status;
        this.address = address;
        this.content = content;
        this.sourceUrl = sourceUrl;
        this.isVisible = isVisible;
        this.applyStartDate = applyStartDate;
        this.applyEndDate = applyEndDate;
    }
}
