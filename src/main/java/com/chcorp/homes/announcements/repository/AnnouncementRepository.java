package com.chcorp.homes.announcements.repository;

import com.chcorp.homes.announcements.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    boolean existsByExternalId(String externalId);

    // targetType으로 조회
    Page<Announcement> findByTargetType(String targetType, Pageable pageable);

    List<Announcement> findAllByTargetType(String targetType, Sort sort);

    // 기존 정확 일치 조회
    Page<Announcement> findByRegion(String region, Pageable pageable);

    Page<Announcement> findByStatus(String status, Pageable pageable);

    Page<Announcement> findByRegionAndStatus(String region, String status, Pageable pageable);

    // 지역 부분 일치 조회
    Page<Announcement> findByRegionContainingIgnoreCase(String region, Pageable pageable);

    Page<Announcement> findByRegionContainingIgnoreCaseAndStatus(
            String region,
            String status,
            Pageable pageable
    );

    // 사용자 화면 기본 목록용
    Page<Announcement> findByIsVisibleTrue(Pageable pageable);

    Page<Announcement> findByRecuitmentTypeAndIsVisibleTrue(
            String recuitmentType,
            Pageable pageable
    );

    Page<Announcement> findByRegionContainingIgnoreCaseAndIsVisibleTrue(
            String region,
            Pageable pageable
    );

    Page<Announcement> findByStatusAndIsVisibleTrue(
            String status,
            Pageable pageable
    );

    Page<Announcement> findByRegionContainingIgnoreCaseAndStatusAndIsVisibleTrue(
            String region,
            String status,
            Pageable pageable
    );

    // =========================
    // 관리자용 통합검색
    // isVisible 여부와 상관없이 전체 공고 조회
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
    // 사용자용 통합검색
    // 사용자 화면에는 isVisible = true인 공고만 노출
    // =========================

    @Query("""
        SELECT a
        FROM Announcement a
        WHERE a.isVisible = true
          AND (
                LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.region) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.address) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.supplyInstitution) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.recuitmentType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
    """)
    Page<Announcement> searchVisibleByKeyword(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("""
        SELECT a
        FROM Announcement a
        WHERE a.isVisible = true
          AND LOWER(a.region) LIKE LOWER(CONCAT('%', :region, '%'))
          AND (
                LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.region) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.address) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.supplyInstitution) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.recuitmentType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
    """)
    Page<Announcement> searchVisibleByKeywordAndRegion(
            @Param("keyword") String keyword,
            @Param("region") String region,
            Pageable pageable
    );

    @Query("""
        SELECT a
        FROM Announcement a
        WHERE a.isVisible = true
          AND a.status = :status
          AND (
                LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.region) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.address) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.supplyInstitution) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.recuitmentType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
    """)
    Page<Announcement> searchVisibleByKeywordAndStatus(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable
    );

    @Query("""
        SELECT a
        FROM Announcement a
        WHERE a.isVisible = true
          AND LOWER(a.region) LIKE LOWER(CONCAT('%', :region, '%'))
          AND a.status = :status
          AND (
                LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.region) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.address) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.supplyInstitution) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.recuitmentType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
    """)
    Page<Announcement> searchVisibleByKeywordAndRegionAndStatus(
            @Param("keyword") String keyword,
            @Param("region") String region,
            @Param("status") String status,
            Pageable pageable
    );

    // =========================
    // 사용자용 마감일 임박 기본 필터
    // =========================

    Page<Announcement> findByApplyEndDateBetweenAndIsVisibleTrue(
            LocalDate today,
            LocalDate deadlineEnd,
            Pageable pageable
    );

    Page<Announcement> findByRegionContainingIgnoreCaseAndApplyEndDateBetweenAndIsVisibleTrue(
            String region,
            LocalDate today,
            LocalDate deadlineEnd,
            Pageable pageable
    );

    Page<Announcement> findByStatusAndApplyEndDateBetweenAndIsVisibleTrue(
            String status,
            LocalDate today,
            LocalDate deadlineEnd,
            Pageable pageable
    );

    Page<Announcement> findByRegionContainingIgnoreCaseAndStatusAndApplyEndDateBetweenAndIsVisibleTrue(
            String region,
            String status,
            LocalDate today,
            LocalDate deadlineEnd,
            Pageable pageable
    );

    // =========================
    // 사용자용 마감일 임박 + 통합검색
    // =========================

    @Query("""
        SELECT a
        FROM Announcement a
        WHERE a.isVisible = true
          AND (
                LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.region) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.address) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.supplyInstitution) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.recuitmentType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
          AND a.applyEndDate BETWEEN :today AND :deadlineEnd
    """)
    Page<Announcement> searchVisibleByKeywordAndDeadline(
            @Param("keyword") String keyword,
            @Param("today") LocalDate today,
            @Param("deadlineEnd") LocalDate deadlineEnd,
            Pageable pageable
    );

    @Query("""
        SELECT a
        FROM Announcement a
        WHERE a.isVisible = true
          AND LOWER(a.region) LIKE LOWER(CONCAT('%', :region, '%'))
          AND (
                LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.region) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.address) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.supplyInstitution) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(a.recuitmentType) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
          AND a.applyEndDate BETWEEN :today AND :deadlineEnd
    """)
    Page<Announcement> searchVisibleByKeywordAndRegionAndDeadline(
            @Param("keyword") String keyword,
            @Param("region") String region,
            @Param("today") LocalDate today,
            @Param("deadlineEnd") LocalDate deadlineEnd,
            Pageable pageable
    );

    @Query("""
        SELECT a
        FROM Announcement a
        WHERE a.isVisible = true
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
    Page<Announcement> searchVisibleByKeywordAndStatusAndDeadline(
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("today") LocalDate today,
            @Param("deadlineEnd") LocalDate deadlineEnd,
            Pageable pageable
    );

    @Query("""
        SELECT a
        FROM Announcement a
        WHERE a.isVisible = true
          AND LOWER(a.region) LIKE LOWER(CONCAT('%', :region, '%'))
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
    Page<Announcement> searchVisibleByKeywordAndRegionAndStatusAndDeadline(
            @Param("keyword") String keyword,
            @Param("region") String region,
            @Param("status") String status,
            @Param("today") LocalDate today,
            @Param("deadlineEnd") LocalDate deadlineEnd,
            Pageable pageable
    );
}