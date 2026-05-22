package com.chcorp.homes.subscription.entity;

import com.chcorp.homes.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "subscription_special_supply_stats",
        indexes = {
                @Index(name = "idx_special_supply_announcement_id", columnList = "announcement_id"),
                @Index(name = "idx_special_supply_pblanc_no", columnList = "pblanc_no"),
                @Index(name = "idx_special_supply_house_manage_no", columnList = "house_manage_no")
        }
)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubscriptionSpecialSupplyStat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "special_supply_stat_id")
    private Long specialSupplyStatId;

    @Column(name = "announcement_id", nullable = false)
    private Long announcementId;

    @Column(name = "house_manage_no", nullable = false)
    private String houseManageNo;

    @Column(name = "pblanc_no", nullable = false)
    private String pblancNo;

    @Column(name = "house_type_name")
    private String houseTypeName;

    @Column(name = "model_no")
    private String modelNo;

    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData;
}