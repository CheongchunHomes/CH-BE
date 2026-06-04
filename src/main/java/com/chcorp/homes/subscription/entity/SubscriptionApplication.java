package com.chcorp.homes.subscription.entity;

import com.chcorp.homes.announcements.entity.Announcement;
import com.chcorp.homes.common.entity.MutableBaseEntity;
import com.chcorp.homes.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "subscription_applications",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_subscription_application_user_announcement",
                        columnNames = {"user_id", "announcement_id"}
                )
        }
)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubscriptionApplication extends MutableBaseEntity {

    @Id
    @SequenceGenerator(
            name = "subscription_application_seq",
            sequenceName = "subscription_application_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscription_application_seq")
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id", nullable = false)
    private Announcement announcement;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionApplicationStatus status;

    @Column(name = "result_at")
    private LocalDateTime resultAt;

    @Column(name = "supply_id")
    private Long supplyId;

    @Column(name = "housing_type")
    private String housingType;

    @Column(name = "applicant_name")
    private String applicantName;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "detail_address")
    private String detailAddress;

    public void applyResult(SubscriptionApplicationStatus status, LocalDateTime resultAt) {
        this.status = status;
        this.resultAt = resultAt;
    }
}
