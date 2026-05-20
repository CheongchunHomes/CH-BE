package com.chcorp.homes.announcements.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AdminAnnouncementRequestDTO {

    private String title;
    private String region;
    private String recuitmentType;
    private String targetType;
    private String status;
    private String address;
    private String content;
    private String sourceUrl;
    private Boolean isVisible;
    private LocalDate applyStartDate;
    private LocalDate applyEndDate;

}
