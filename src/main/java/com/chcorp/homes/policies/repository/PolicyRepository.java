package com.chcorp.homes.policies.repository;

import com.chcorp.homes.policies.entity.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    // 외부 API 데이터 중복 저장 방지용
    boolean existsBySourceTypeAndExternalId(String sourceType, String externalId);

    // 외부 API 데이터 수정/갱신 시 사용할 수 있음
    Optional<Policy> findBySourceTypeAndExternalId(String sourceType, String externalId);

    // 사용자 화면에는 노출 여부가 true인 정책만 조회
    Page<Policy> findByIsVisibleTrue(Pageable pageable);

    // 대분류 필터
    Page<Policy> findByIsVisibleTrueAndMainCategory(String mainCategory, Pageable pageable);

    // 대분류 + 소분류 필터
    Page<Policy> findByIsVisibleTrueAndMainCategoryAndSubCategory(String mainCategory, String subCategory, Pageable pageable);

    List<Policy> findByIsVisibleTrue(Sort sort);

    // 관리자 전체 조회 (isVisible 여부 상관없이)
    Page<Policy> findAll(Pageable pageable);

    // 상태필터
    Page<Policy> findByStatus(String status, Pageable pageable);

    // 대분류 필터
    Page<Policy> findByMainCategory(String mainCategory, Pageable pageable);

    // 대분류 + 상태필터
    Page<Policy> findByMainCategoryAndStatus(String mainCategory, String status, Pageable pageable);

    // 대분류 + 소분류 필터
    Page<Policy> findByMainCategoryAndSubCategory(String mainCategory, String subCategory, Pageable pageable);
    
    // 대분류 + 소분류 + 상태
    Page<Policy> findByMainCategoryAndSubCategoryAndStatus(String mainCategory, String subCategory, String status, Pageable pageable);
   
    // 소분류
    Page<Policy> findBySubCategory(String subCategory, Pageable pageable);
}
