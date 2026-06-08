package com.chcorp.homes.banner.entity;

import com.chcorp.homes.common.entity.MutableBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "banners")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Banner extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // 관리용 배너 명칭 (공지 제목 또는 커스텀)

    @Column(length = 2000)
    private String content; // 배너 본문 요약 텍스트 (공지 summary 또는 커스텀 홍보 문구)

    @Column(name = "notice_id")
    private Long noticeId; // 연결된 공지 ID (선택사항, 공지 삭제 시 null 처리)

    @Column(name = "link_url")
    private String linkUrl; // 배너 클릭 시 이동할 링크 (제도/공고 페이지 등)

    @Column(name = "is_visible", nullable = false)
    private boolean isVisible = true; // 수동 노출 토글 여부

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate; // 자동 게시 시작 일시

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate; // 자동 게시 종료 일시

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder; // 노출 우선순위 (낮을수록 먼저 노출)


    // 배너 수정
    public void update(String title, String content, Long noticeId, String linkUrl,
                       LocalDateTime startDate, LocalDateTime endDate,
                       Integer sortOrder, boolean isVisible) {
        this.title = title;
        this.content = content;
        this.noticeId = noticeId;
        this.linkUrl = linkUrl;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sortOrder = sortOrder;
        this.isVisible = isVisible;

    }

    // 노출 여부 토글
    public void toggleVisibility() {
        this.isVisible = !this.isVisible;
    }
}