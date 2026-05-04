package com.chcorp.homes.announcements.dto;

import com.chcorp.homes.announcements.entity.Announcement;
import lombok.Getter;

import java.time.LocalDate;

@Getter
// 공고 관련 DTO
public class AnnouncementListDTO {
    private Long announcementId;
    private String title;
    private String region;
    private String recuitmentType;
    private String status;
    private LocalDate applyStartDate;
    private LocalDate applyEndDate;
    private String address;
    private String targetType;
    private String supplyInstitution;
    private String totHshldCo;
    private Integer rentGtn;
    private Integer mtRntchrg;
    private String heatMthdNm;
    private LocalDate beginDe;
    private LocalDate endDe;
    private String content;

    //Entity -> DTO 변환 생성자
    public AnnouncementListDTO(Announcement entity) {
        this.announcementId = entity.getAnnouncementId();
        this.title = entity.getTitle();
        this.region = entity.getRegion();
        this.recuitmentType = entity.getRecuitmentType();
        this.status = entity.getStatus();
        this.applyStartDate = entity.getApplyStartDate();
        this.applyEndDate = entity.getApplyEndDate();
        this.address = entity.getAddress();
        this.targetType = entity.getTargetType();
        this.supplyInstitution = entity.getSupplyInstitution();
        this.totHshldCo = entity.getTotHshldCo();
        this.rentGtn = entity.getRentGtn();
        this.mtRntchrg = entity.getMtRntchrg();
        this.heatMthdNm = entity.getHeatMthdNm();
        this.beginDe = entity.getBeginDe();
        this.endDe = entity.getEndDe();
        this.content = entity.getContent();
    }
}
