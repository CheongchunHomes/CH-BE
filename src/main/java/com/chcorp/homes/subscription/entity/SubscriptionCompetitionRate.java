package com.chcorp.homes.subscription.entity;

import com.chcorp.homes.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "subscription_competition_rates",
        indexes = {
                @Index(name = "idx_competition_announcement_id", columnList = "announcement_id"),
                @Index(name = "idx_competition_rank_no", columnList = "rank_no"),
                @Index(name = "idx_competition_pblanc_no", columnList = "pblanc_no"),
                @Index(name = "idx_competition_house_manage_no", columnList = "house_manage_no")
        }
)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubscriptionCompetitionRate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "competition_rate_id")
    private Long competitionRateId;

    @Column(name = "announcement_id", nullable = false)
    private Long announcementId;

    @Column(name = "house_manage_no", nullable = false)
    private String houseManageNo;

    @Column(name = "pblanc_no", nullable = false)
    private String pblancNo;

    @Column(name = "model_no")
    private String modelNo;

    @Column(name = "house_type_name")
    private String houseTypeName;

    @Column(name = "supply_count")
    private Integer supplyCount;

    @Column(name = "rank_no")
    private Integer rankNo;

    @Column(name = "residence_code")
    private Integer residenceCode;

    @Column(name = "residence_area")
    private String residenceArea;

    @Column(name = "request_count")
    private Integer requestCount;

    @Column(name = "competition_rate")
    private String competitionRate;

    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData;
}