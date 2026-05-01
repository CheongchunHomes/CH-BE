package com.chcorp.homes.announcement.entity;

import com.chcorp.homes.common.entity.BaseEntity;
import com.chcorp.homes.common.entity.MutableBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;



@Entity
@Table(name = "announcements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Announcement extends MutableBaseEntity {
    @Id
    @Column(name = "announcement_id")
    private Long announcementId;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "source_type", nullable = false)
    private String sourceType;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "region")
    private String region;

    @Column(name = "recruitment_type")
    private String recruitmentType;

    @Column(name = "target_type")
    private String targetType;

    @Column(name = "requires_sub_account")
    private Boolean requiresSubAccount;

    @Column(name = "income_condition")
    private String incomeCondition;

    @Column(name = "special_supply_yn")
    private Boolean specialSupplyYn;

    @Column(name = "content", columnDefinition = "bytea")
    private byte[] content;

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
}


