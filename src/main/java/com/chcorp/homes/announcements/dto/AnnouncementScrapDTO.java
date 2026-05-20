package com.chcorp.homes.announcements.dto;

import com.chcorp.homes.announcements.entity.Announcement;
import com.chcorp.homes.announcements.entity.AnnouncementScrap;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
public class AnnouncementScrapDTO {

    private Long scrapId;
    private Long announcementId;
    private String title;
    private String region;
    private String status;
    private String recuitmentType;
    private String targetType;
    private String supplyInstitution;
    private LocalDate applyStartDate;
    private LocalDate applyEndDate;
    private Instant scrapedAt;
    private Boolean announcementVisible;

    public AnnouncementScrapDTO(AnnouncementScrap scrap) {
        Announcement announcement = scrap.getAnnouncement();

        this.scrapId = scrap.getScrapId();
        this.announcementId = announcement.getAnnouncementId();
        this.title = announcement.getTitle();
        this.region = announcement.getRegion();
        this.status = announcement.getStatus();
        this.recuitmentType = announcement.getRecuitmentType();
        this.targetType = announcement.getTargetType();
        this.supplyInstitution = announcement.getSupplyInstitution();
        this.applyStartDate = announcement.getApplyStartDate();
        this.applyEndDate = announcement.getApplyEndDate();
        this.scrapedAt = scrap.getCreatedAt();
        this.announcementVisible = scrap.getAnnouncement().getIsVisible();
    }
}
