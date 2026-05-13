package com.chcorp.homes.announcements.entity;

import com.chcorp.homes.common.entity.BaseEntity;
import com.chcorp.homes.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@ToString(exclude = {"user", "announcement"})
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "announcement_scraps",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_announcement_scrap_user_announcement",
                    columnNames = {"user_id", "announcement_id"}
            )
        })
public class AnnouncementScrap extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scrap_id")
    private Long scrapId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id", nullable = false)
    private Announcement announcement;

}
