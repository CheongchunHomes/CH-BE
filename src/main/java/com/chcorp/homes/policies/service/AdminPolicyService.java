package com.chcorp.homes.policies.service;

import com.chcorp.homes.policies.dto.AdminPolicyRequestDTO;
import com.chcorp.homes.policies.entity.Policy;
import com.chcorp.homes.policies.repository.PolicyRepository;
import com.chcorp.homes.policies.repository.PolicyScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminPolicyService {

    private final PolicyRepository policyRepository;
    private final PolicyService policyService;
    private final PolicyScrapRepository policyScrapRepository;

    // =========================
    // 목록 조회 (관리자 - 전체, isVisible 무관)
    // =========================
    @Transactional(readOnly = true)
    public Page<Policy> getList(String keyword, String mainCategory,
                                String subCategory, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "policyId"));

        mainCategory = (mainCategory == null || mainCategory.isBlank()) ? null : mainCategory.trim();
        subCategory = (subCategory == null || subCategory.isBlank()) ? null : subCategory.trim();
        status = (status == null || status.isBlank()) ? null : status.trim();
        keyword = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

        if (keyword != null) {
            String finalKeyword = keyword;
            String finalMainCategory = mainCategory;
            String finalSubCategory = subCategory;
            String finalStatus = status;

            List<Policy> filtered = policyRepository
                    .findAll(Sort.by(Sort.Direction.DESC, "policyId"))
                    .stream()
                    .filter(p -> containsIgnoreCase(p.getTitle(), finalKeyword)
                            || containsIgnoreCase(p.getMainCategory(), finalKeyword)
                            || containsIgnoreCase(p.getSubCategory(), finalKeyword))
                    .filter(p -> finalMainCategory == null || finalMainCategory.equals(p.getMainCategory()))
                    .filter(p -> finalSubCategory == null || finalSubCategory.equals(p.getSubCategory()))
                    .filter(p -> finalStatus == null || finalStatus.equals(p.getStatus()))
                    .toList();

            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), filtered.size());
            List<Policy> pageContent = start >= filtered.size()
                    ? List.of() : filtered.subList(start, end);

            return new PageImpl<>(pageContent, pageable, filtered.size());
        }

        if (mainCategory != null && subCategory != null && status != null) {
            return policyRepository.findByMainCategoryAndSubCategoryAndStatus(mainCategory, subCategory, status, pageable);
        }

        if (mainCategory != null && subCategory != null) {
            return policyRepository.findByMainCategoryAndSubCategory(mainCategory, subCategory, pageable);
        }

        if (mainCategory != null && status != null) {
            return policyRepository.findByMainCategoryAndStatus(mainCategory, status, pageable);
        }

        if (mainCategory != null) {
            return policyRepository.findByMainCategory(mainCategory, pageable);
        }

        if (subCategory != null) {
            return policyRepository.findBySubCategory(subCategory, pageable);
        }

        if (status != null) {
            return policyRepository.findByStatus(status, pageable);
        }

        return policyRepository.findAll(pageable);
    }

    // =========================
    // 단건 조회
    // =========================
    @Transactional(readOnly = true)
    public Policy getOne(Long id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("지원제도를 찾을 수 없습니다."));
    }

    // =========================
    // 등록
    // =========================
    @Transactional
    public void register(AdminPolicyRequestDTO dto) {
        Policy policy = Policy.builder()
                .title(dto.getTitle())
                .mainCategory(dto.getMainCategory())
                .subCategory(dto.getSubCategory())
                .status(dto.getStatus())
                .content(dto.getContent())
                .sourceUrl(dto.getSourceUrl())
                .onlineApplyUrl(dto.getOnlineApplyUrl())
                .supervisingInstitution(dto.getSupervisingInstitution())
                .summary(dto.getSummary())
                .targetDesc(dto.getTargetDesc())
                .applyMethod(dto.getApplyMethod())
                .requiredDocuments(dto.getRequiredDocuments())
                .applyPeriod(dto.getApplyPeriod())
                .supportType(dto.getSupportType())
                .isVisible(dto.getIsVisible() != null ? dto.getIsVisible() : true)
                .sourceType("관리자등록")
                .build();

        policyRepository.save(policy);
    }

    // =========================
    // 수정
    // =========================
    @Transactional
    public void update(Long id, AdminPolicyRequestDTO dto) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("지원제도를 찾을 수 없습니다."));

        policy.updateAdminFields(
                dto.getTitle(),
                dto.getMainCategory(),
                dto.getSubCategory(),
                dto.getStatus(),
                dto.getContent(),
                dto.getSourceUrl(),
                dto.getOnlineApplyUrl(),
                dto.getSupervisingInstitution(),
                dto.getSummary(),
                dto.getTargetDesc(),
                dto.getApplyMethod(),
                dto.getRequiredDocuments(),
                dto.getApplyPeriod(),
                dto.getSupportType(),
                dto.getIsVisible()
        );
    }

    // =========================
    // 노출 여부 변경
    // =========================
    @Transactional
    public void updateVisibility(Long id, Boolean isVisible) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("지원제도를 찾을 수 없습니다."));

        policy.updateVisibility(isVisible);
    }

    // ==================
    // 삭제
    // 관리자등록 제도 + 스크랩 없음 → 실제 삭제
    // API 제도 또는 스크랩된 제도 → 숨김처리
    // ==================
    @Transactional
    public void delete(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("지원제도를 찾을 수 없습니다."));

        boolean hasScrap = policyScrapRepository.existsByPolicy_PolicyId(id);

        // API로 수집된 제도는 실제 삭제하지 않고 숨김처리
        if (!"관리자등록".equals(policy.getSourceType())) {
            policy.updateVisibility(false);
            return;
        }

        // 관리자등록 제도라도 스크랩되어 있으면 실제 삭제하지 않고 숨김처리
        if (hasScrap) {
            policy.updateVisibility(false);
            return;
        }

        // 관리자등록 + 스크랩 없음일 때만 실제 삭제
        policyRepository.delete(policy);
    }

    // =========================
    // 외부 API 트리거
    // =========================
    @Transactional
    public void triggerFetchYouthPolicies() {
        policyService.fetchYouthPolicies();
    }

    @Transactional
    public void triggerFetchPublicServices() {
        policyService.fetchPublicServices();
    }

    // =========================
    // 유틸
    // =========================
    private boolean containsIgnoreCase(String value, String keyword) {
        if (value == null || keyword == null) return false;
        return value.toLowerCase().contains(keyword.toLowerCase());
    }
}