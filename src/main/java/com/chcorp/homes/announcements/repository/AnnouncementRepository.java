package com.chcorp.homes.announcements.repository;

import com.chcorp.homes.announcements.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    boolean existsByExternalId(String externalId);

    // 기존 정확 일치 조회(유지)
    Page<Announcement> findByRegion(String region, Pageable pageable);

    Page<Announcement> findByStatus(String status, Pageable pageable);

    Page<Announcement> findByRegionAndStatus(String region, String status, Pageable pageable);

    // 필터 부분 일치(contains) 및 대소문자 무시 조회 - 프론트에서 '서울'과 같이 일부 키워드로도 매칭되게 하기 위함
    Page<Announcement> findByRegionContainingIgnoreCase(String region, Pageable pageable);

    Page<Announcement> findByRegionContainingIgnoreCaseAndStatus(String region, String status, Pageable pageable);

    Page<Announcement> findByIsVisibleTrue(Pageable pageable);

    Page<Announcement> findByRecuitmentTypeAndIsVisibleTrue(
            String recuitmentType,
            Pageable pageable
    );

    // =========================
    // 기존 통합검색
    // =========================

    @Query("""
        SELECT a
        FROM Announcement a
        WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(a.region) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(a.address) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(a.supplyInstitution) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(a.recuitmentType) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<Announcement> searchByKeyword(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("""
        SELECT a
        FROM Announcement a
        WHERE LOWER(a.region) LIKE LOWER(CONCAT('%', :region, '%'))
          AND (
                LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.region) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.address) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.supplyInstitution) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.recuitmentType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
    """)
    Page<Announcement> searchByKeywordAndRegion(
            @Param("keyword") String keyword,
            @Param("region") String region,
            Pageable pageable
    );

    @Query("""
        SELECT a
        FROM Announcement a
        WHERE a.status = :status
          AND (
                LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.region) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.address) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.supplyInstitution) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.recuitmentType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
    """)
    Page<Announcement> searchByKeywordAndStatus(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable
    );

    @Query("""
        SELECT a
        FROM Announcement a
        WHERE LOWER(a.region) LIKE LOWER(CONCAT('%', :region, '%'))
          AND a.status = :status
          AND (
                LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.region) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.address) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.supplyInstitution) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.recuitmentType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
    """)
    Page<Announcement> searchByKeywordAndRegionAndStatus(
            @Param("keyword") String keyword,
            @Param("region") String region,
            @Param("status") String status,
            Pageable pageable
    );

    // =========================
    // 마감일 임박 기본 필터
    // =========================

    Page<Announcement> findByApplyEndDateBetween(
            LocalDate today,
            LocalDate deadlineEnd,
            Pageable pageable
    );

    Page<Announcement> findByRegionContainingIgnoreCaseAndApplyEndDateBetween(
            String region,
            LocalDate today,
            LocalDate deadlineEnd,
            Pageable pageable
    );

    Page<Announcement> findByStatusAndApplyEndDateBetween(
            String status,
            LocalDate today,
            LocalDate deadlineEnd,
            Pageable pageable
    );

    Page<Announcement> findByRegionContainingIgnoreCaseAndStatusAndApplyEndDateBetween(
            String region,
            String status,
            LocalDate today,
            LocalDate deadlineEnd,
            Pageable pageable
    );

    // =========================
    // 마감일 임박 + 통합검색
    // =========================

    @Query("""
        SELECT a
        FROM Announcement a
        WHERE (
                LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.region) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.address) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.supplyInstitution) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.recuitmentType) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
          AND a.applyEndDate BETWEEN :today AND :deadlineEnd
    """)
    Page<Announcement> searchByKeywordAndDeadline(
            @Param("keyword") String keyword,
            @Param("today") LocalDate today,
            @Param("deadlineEnd") LocalDate deadlineEnd,
            Pageable pageable
    );

    @Query("""
        SELECT a
        FROM Announcement a
        WHERE LOWER(a.region) LIKE LOWER(CONCAT('%', :region, '%'))
          AND (
                LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.region) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.address) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.supplyInstitution) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.recuitmentType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
          AND a.applyEndDate BETWEEN :today AND :deadlineEnd
    """)
    Page<Announcement> searchByKeywordAndRegionAndDeadline(
            @Param("keyword") String keyword,
            @Param("region") String region,
            @Param("today") LocalDate today,
            @Param("deadlineEnd") LocalDate deadlineEnd,
            Pageable pageable
    );

    @Query("""
        SELECT a
        FROM Announcement a
        WHERE a.status = :status
          AND (
                LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.region) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.address) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.supplyInstitution) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.recuitmentType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
          AND a.applyEndDate BETWEEN :today AND :deadlineEnd
    """)
    Page<Announcement> searchByKeywordAndStatusAndDeadline(
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("today") LocalDate today,
            @Param("deadlineEnd") LocalDate deadlineEnd,
            Pageable pageable
    );

    @Query("""
        SELECT a
        FROM Announcement a
        WHERE LOWER(a.region) LIKE LOWER(CONCAT('%', :region, '%'))
          AND a.status = :status
          AND (
                LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.region) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.address) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.supplyInstitution) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.recuitmentType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
          AND a.applyEndDate BETWEEN :today AND :deadlineEnd
    """)
    Page<Announcement> searchByKeywordAndRegionAndStatusAndDeadline(
            @Param("keyword") String keyword,
            @Param("region") String region,
            @Param("status") String status,
            @Param("today") LocalDate today,
            @Param("deadlineEnd") LocalDate deadlineEnd,
            Pageable pageable
    );
}