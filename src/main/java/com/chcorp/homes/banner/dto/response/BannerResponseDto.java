package com.chcorp.homes.banner.dto.response;

import com.chcorp.homes.banner.entity.Banner;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * 타임리프 화면 및 프론트엔드 API 응답으로 배너 정보를 안전하게 내보내기 위한 데이터 구조
 * 보안 및 아키텍처 규칙 준수를 위해 엔티티 객체를 직접 노출하지 않고 이 DTO를 거쳐 출력
 */
public record BannerResponseDto(
        Long id,
        String title,            // 배너 명칭
        String content,          // 배너 본문 요약 텍스트 (홍보 문구)
        String linkUrl,          // 클릭 시 이동할 링크 주소
        LocalDateTime startDate, // 게시 시작 일시
        LocalDateTime endDate,   // 게시 종료 일시
        Integer sortOrder,       // 노출 우선순위 (낮을수록 먼저 노출)
        boolean isVisible,       // 현재 수동 노출 활성화 여부
        Instant createdAt        // 등록 일시
) {
    /**
     * 정적 팩토리 메서드: 엔티티 객체를 안전하게 Response DTO 구조로 매핑
     */
    public static BannerResponseDto from(Banner banner) {
        return new BannerResponseDto(
                banner.getId(),
                banner.getTitle(),
                banner.getContent(),
                banner.getLinkUrl(),
                banner.getStartDate(),
                banner.getEndDate(),
                banner.getSortOrder(),
                banner.isVisible(),
                banner.getCreatedAt()
        );
    }
}