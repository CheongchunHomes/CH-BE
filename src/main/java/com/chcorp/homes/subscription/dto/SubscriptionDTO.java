package com.chcorp.homes.subscription.dto;

import com.chcorp.homes.announcements.entity.Announcement;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@Builder
public class SubscriptionDTO {

    private Long id;
    private String type;
    private String title;
    private String region;
    private LocalDate applyStartDate;
    private LocalDate applyEndDate;
    private String status;
    private String address;
    private String recruitmentType;
    private String sourceType;

    public static SubscriptionDTO from(Announcement announcement) {
        return SubscriptionDTO.builder()
                .id(announcement.getAnnouncementId())
                .type(announcement.getTargetType())
                .title(announcement.getTitle())
                .region(announcement.getRegion())
                .applyStartDate(announcement.getApplyStartDate())
                .applyEndDate(announcement.getApplyEndDate())
                .status(announcement.getStatus())
                .address(announcement.getAddress())
                .recruitmentType(announcement.getRecuitmentType())
                .sourceType(announcement.getSourceType())
                .build();
    }
}