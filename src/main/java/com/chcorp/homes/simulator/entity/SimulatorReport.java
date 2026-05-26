package com.chcorp.homes.simulator.entity;

import com.chcorp.homes.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * 시뮬레이터 AI 분석 리포트 엔티티
 * 탭04 AI 청춘 플래너 분석 결과를 저장한다
 */
@Entity
@Table(name = "simulator_reports")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SimulatorReport extends BaseEntity {

    // 리포트 PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    // 유저 PK
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 탭01 자산 플랜 스냅샷
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "asset_snapshot", columnDefinition = "jsonb")
    private Object assetSnapshot;

    // 탭02 주거 비교 스냅샷
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "housing_snapshot", columnDefinition = "jsonb")
    private Object housingSnapshot;

    // 탭03 금융 체감 스냅샷
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "loan_snapshot", columnDefinition = "jsonb")
    private Object loanSnapshot;

    // 제도 추천 점수 스냅샷 (고도화 예정)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "score_snapshot", columnDefinition = "jsonb")
    private Object scoreSnapshot;

    // AI 분석 결과
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ai_result", columnDefinition = "jsonb")
    private Object aiResult;

    // 분석 시 사용된 AI 프롬프트
    @Column(name = "ai_prompt", columnDefinition = "TEXT")
    private String aiPrompt;

    // PDF 파일 경로 (미구현)
    @Column(name = "pdf_url")
    private String pdfUrl;
}