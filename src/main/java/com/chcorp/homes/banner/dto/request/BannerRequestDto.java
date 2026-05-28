package com.chcorp.homes.banner.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 관리자 페이지 등록/수정 폼에서 서버로 전송되는 배너 요청 데이터 구조
 * @ModelAttribute 바인딩을 위해 record 대신 class 사용 (기본 생성자 + setter 필요)
 */
@Getter
@Setter
@NoArgsConstructor
public class BannerRequestDto {

        @NotBlank(message = "배너 제목은 필수입니다.")
        private String title; // 배너 제목 (공지 제목 자동입력 또는 커스텀)

        private String content; // 배너 본문 요약 (공지 summary 자동입력 또는 커스텀 홍보 문구)

        private Long noticeId; // 연결된 공지 ID (선택사항)

        private String linkUrl; // 배너 클릭 시 이동할 링크

        @NotNull(message = "게시 시작일은 필수입니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        private LocalDateTime startDate; // 배너 게시 예약 시작 일시

        @NotNull(message = "게시 종료일은 필수입니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        private LocalDateTime endDate; // 배너 게시 예약 종료 일시

        @NotNull(message = "노출 순서는 필수입니다.")
        private Integer sortOrder; // 메인 화면 노출 우선순위 (번호가 낮을수록 맨 앞에 배치됨)

        private boolean visible; // 최초 등록 시 마스터 노출 활성화 여부

}