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
                // DB 상태값 대신 날짜 기준으로 현재 상태를 다시 계산한다.
                .status(resolveStatus(announcement.getApplyStartDate(), announcement.getApplyEndDate()))
                .address(announcement.getAddress())
                .recruitmentType(announcement.getRecuitmentType())
                .sourceType(announcement.getSourceType())
                .build();
    }

    private static String resolveStatus(LocalDate applyStartDate, LocalDate applyEndDate) {
        // 날짜가 없으면 상태를 확정하지 않는다.
        if (applyStartDate == null || applyEndDate == null) {
            return "상태 미정";
        }

        LocalDate today = LocalDate.now();

        // 접수 시작 전이면 접수예정으로 표시한다.
        if (today.isBefore(applyStartDate)) {
            return "접수예정";
        }

        // 접수 시작일과 마감일 사이면 접수중으로 표시한다.
        if (!today.isAfter(applyEndDate)) {
            return "접수중";
        }

        // 마감일이 지난 공고는 마감으로 표시한다.
        return "마감";
    }
}