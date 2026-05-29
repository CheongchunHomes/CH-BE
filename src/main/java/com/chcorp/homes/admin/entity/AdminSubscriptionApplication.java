package com.chcorp.homes.admin.entity;

import com.chcorp.homes.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_application")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AdminSubscriptionApplication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_app_id")
    private Long subAppId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "announcement_id", nullable = false)
    private Long announcementId;

    @Column(name="status")
    private String status;

    @Column(name="applied_at")
    private LocalDateTime appliedAt;

    @Column(name = "result_at")
    private LocalDateTime resultAt;

    public void updateResult(String status, LocalDateTime resultAt) {
        this.status = status;
        this.resultAt = resultAt;
    }
}
