package com.chcorp.homes.announcement.dto;

import com.chcorp.homes.announcement.entity.Announcement;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AnnouncementListResponse {

    private Long id;
    private String type;
    private String title;
    private String region;
    private String date;
    private String status;
    private String address;
    private String recruitmentType;
    private String sourceType;

    public static AnnouncementListResponse from(Announcement announcement) {
        return AnnouncementListResponse.builder()
                .id(announcement.getAnnouncementId())
                .type(announcement.getTargetType())
                .title(announcement.getTitle())
                .region(announcement.getRegion())
                .date(
                        announcement.getApplyStartDate() != null
                                ? announcement.getApplyStartDate().toString()
                                : null
                )
                .status(announcement.getStatus())
                .address(announcement.getAddress())
                .recruitmentType(announcement.getRecruitmentType())
                .sourceType(announcement.getSourceType())
                .build();
    }
}