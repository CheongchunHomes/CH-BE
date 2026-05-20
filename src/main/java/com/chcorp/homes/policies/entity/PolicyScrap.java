package com.chcorp.homes.policies.entity;

import com.chcorp.homes.common.entity.BaseEntity;
import com.chcorp.homes.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@ToString(exclude = {"user", "policy"})
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "policy_scraps",
    uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_policy_scrap_user_policy",
                    columnNames = {"user_id", "policy_id"}
            )
    })
public class PolicyScrap extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scrap_id")
    private Long scrapId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;
}
