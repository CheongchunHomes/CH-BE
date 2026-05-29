package com.chcorp.homes.simulator.entity;

import com.chcorp.homes.common.entity.MutableBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "asset_plans")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AssetPlan extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long planId;

    /* 회원 ID */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /* 플랜 유형 (HOUSING/TRAVEL/CAR/ELECTRONICS/WEDDING/FASHION/EDUCATION/OTHER) */
    @Column(name = "category", nullable = false, length = 50)
    private String category;

    /* 플랜명 */
    @Column(name = "plan_name", nullable = false)
    private String planName;

    /* 현재 모은 금액 */
    @Column(name = "base_asset")
    private Long baseAsset;

    /* 목표 금액 */
    @Column(name = "goal_amount")
    private Long goalAmount;

    /* 저축 시작일 */
    @Column(name = "start_date")
    private LocalDate startDate;

    /* 저축 종료일 (목표일) */
    @Column(name = "end_date")
    private LocalDate endDate;

    /* 월 저축액 (자동 계산 후 저장) */
    @Column(name = "monthly_saving")
    private Long monthlySaving;

    /* 달성 여부 */
    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted;

    // 플랜 수정 메서드
    public void update(String category, String planName, Long baseAsset, Long goalAmount,
                       LocalDate startDate, LocalDate endDate, Long monthlySaving, Boolean isCompleted) {
        this.category = category;
        this.planName = planName;
        this.baseAsset = baseAsset;
        this.goalAmount = goalAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.monthlySaving = monthlySaving;
        this.isCompleted = isCompleted;
    }
}