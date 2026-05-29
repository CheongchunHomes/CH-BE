package com.chcorp.homes.subscription.entity;

import com.chcorp.homes.announcements.entity.Announcement;
import com.chcorp.homes.common.entity.MutableBaseEntity;
import com.chcorp.homes.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
}
