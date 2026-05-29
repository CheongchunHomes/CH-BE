package com.chcorp.homes.notice.entity;

import com.chcorp.homes.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "notice")
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long noticeId;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 50)
    private String summary;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean important;

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void updateFromAdmin(
            String category,
            String title,
            String summary,
            String content,
            boolean important
    ) {
        this.category = category;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.important = important;
    }
}