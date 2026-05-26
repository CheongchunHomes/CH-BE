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

    // 노출 중이고 마감일이 오늘 이후인 공고만 조회
    Page<Announcement> findByIsVisibleTrueAndApplyEndDateGreaterThanEqual(
            LocalDate today,
            Pageable pageable
    );

    // 모집유형별로 노출 중이고 마감일이 오늘 이후인 공고만 조회
    Page<Announcement> findByRecuitmentTypeAndIsVisibleTrueAndApplyEndDateGreaterThanEqual(
            String recuitmentType,
            LocalDate today,
            Pageable pageable
    );

    // 출처와 모집유형이 일치하고 마감되지 않은 공고만 조회
    Page<Announcement> findBySourceTypeAndRecuitmentTypeAndIsVisibleTrueAndApplyEndDateGreaterThanEqual(
            String sourceType,
            String recuitmentType,
            LocalDate today,
            Pageable pageable
    );

    // 여러 출처에 해당하고 마감되지 않은 공고만 조회
    Page<Announcement> findBySourceTypeInAndIsVisibleTrueAndApplyEndDateGreaterThanEqual(
            List<String> sourceTypes,
            LocalDate today,
            Pageable pageable
    );

    // 특별공급 물량이 있는 마감 전 APT 공고만 조회한다.
    @Query("""
    SELECT DISTINCT a
    FROM Announcement a
    WHERE a.sourceType = :sourceType
      AND a.recuitmentType = :recuitmentType
      AND a.isVisible = true
      AND a.applyEndDate >= :today
      AND EXISTS (
          SELECT 1
          FROM SubscriptionHouseType h
          WHERE TRIM(h.pblancNo) = TRIM(a.pblancNo)
            AND h.specialSupplyCount IS NOT NULL
            AND h.specialSupplyCount > 0
      )
    """)
    Page<Announcement> findAptSpecialSupplyAnnouncements(
            @Param("sourceType") String sourceType,
            @Param("recuitmentType") String recuitmentType,
            @Param("today") LocalDate today,
            Pageable pageable
    );

    // 일반공급 물량이 있는 마감 전 APT 공고만 조회한다.
// 화면에서는 1순위/2순위 묶음으로 보여준다.
    @Query("""
    SELECT DISTINCT a
    FROM Announcement a
    WHERE a.sourceType = :sourceType
      AND a.recuitmentType = :recuitmentType
      AND a.isVisible = true
      AND a.applyEndDate >= :today
      AND EXISTS (
          SELECT 1
          FROM SubscriptionHouseType h
          WHERE TRIM(h.pblancNo) = TRIM(a.pblancNo)
            AND h.generalSupplyCount IS NOT NULL
            AND h.generalSupplyCount > 0
      )
    """)
    Page<Announcement> findAptGeneralAnnouncements(
            @Param("sourceType") String sourceType,
            @Param("recuitmentType") String recuitmentType,
            @Param("today") LocalDate today,
            Pageable pageable
    );
    // 지도에 표시할 수 있는 청약 공고만 조회한다.
    @Query("""
        SELECT a
        FROM Announcement a
        WHERE a.isVisible = true
          AND a.latitude IS NOT NULL
          AND a.longitude IS NOT NULL
          AND a.applyStartDate IS NOT NULL
          AND a.applyEndDate IS NOT NULL
          AND a.applyStartDate <= :today
          AND a.applyEndDate >= :today
        ORDER BY a.applyEndDate ASC
        """)
    List<Announcement> findApplyingNowMapAnnouncements(@Param("today") LocalDate today);

    // 주소는 있지만 좌표가 아직 없는 공고를 좌표 변환 대상으로 조회한다.
    @Query("""
        SELECT a
        FROM Announcement a
        WHERE a.isVisible = true
          AND a.address IS NOT NULL
          AND TRIM(a.address) <> ''
          AND (a.latitude IS NULL OR a.longitude IS NULL)
        ORDER BY a.announcementId ASC
    """)
    List<Announcement> findGeocodeTargets(Pageable pageable);

}
