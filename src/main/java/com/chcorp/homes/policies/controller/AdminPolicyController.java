package com.chcorp.homes.policies.controller;

import com.chcorp.homes.policies.dto.AdminPolicyRequestDTO;
import com.chcorp.homes.policies.entity.Policy;
import com.chcorp.homes.policies.service.AdminPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/policies")
public class AdminPolicyController {

    private final AdminPolicyService adminPolicyService;

    // =========================
    // 목록
    // =========================
    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "") String mainCategory,
                       @RequestParam(defaultValue = "") String subCategory,
                       @RequestParam(defaultValue = "") String status,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {

        Page<Policy> result = adminPolicyService
                .getList(keyword, mainCategory, subCategory, status, page, 10);

        model.addAttribute("policies", result);
        model.addAttribute("keyword", keyword);
        model.addAttribute("mainCategory", mainCategory);
        model.addAttribute("subCategory", subCategory);
        model.addAttribute("status", status);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", result.getTotalPages());

        return "admin/policies/list";
    }

    // =========================
    // 등록 폼
    // =========================
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new AdminPolicyRequestDTO());
        model.addAttribute("isEdit", false);
        return "admin/policies/form";
    }

    // =========================
    // 등록 처리
    // =========================
    @PostMapping
    public String create(@ModelAttribute("form") AdminPolicyRequestDTO dto) {
        adminPolicyService.register(dto);
        return "redirect:/admin/policies";
    }

    // =========================
    // 수정 폼
    // =========================
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Policy policy = adminPolicyService.getOne(id);

        AdminPolicyRequestDTO form = new AdminPolicyRequestDTO();
        form.setTitle(policy.getTitle());
        form.setMainCategory(policy.getMainCategory());
        form.setSubCategory(policy.getSubCategory());
        form.setStatus(policy.getStatus());
        form.setContent(policy.getContent());
        form.setSourceUrl(policy.getSourceUrl());
        form.setOnlineApplyUrl(policy.getOnlineApplyUrl());
        form.setSupervisingInstitution(policy.getSupervisingInstitution());
        form.setSummary(policy.getSummary());
        form.setTargetDesc(policy.getTargetDesc());
        form.setApplyMethod(policy.getApplyMethod());
        form.setRequiredDocuments(policy.getRequiredDocuments());
        form.setApplyPeriod(policy.getApplyPeriod());
        form.setSupportType(policy.getSupportType());
        form.setIsVisible(policy.getIsVisible());

        model.addAttribute("form", form);
        model.addAttribute("isEdit", true);
        model.addAttribute("policyId", id);
        return "admin/policies/form";
    }

    // =========================
    // 수정 처리
    // =========================
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute("form") AdminPolicyRequestDTO dto) {
        adminPolicyService.update(id, dto);
        return "redirect:/admin/policies";
    }

    // =========================
    // 노출 여부 변경
    // =========================
    @PostMapping("/{id}/visibility")
    public String updateVisibility(@PathVariable Long id,
                                   @RequestParam Boolean isVisible) {
        adminPolicyService.updateVisibility(id, isVisible);
        return "redirect:/admin/policies";
    }

    // =========================
    // 삭제
    // =========================
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        adminPolicyService.delete(id);
        return "redirect:/admin/policies";
    }

    // =========================
    // 외부 API 트리거
    // =========================
    @PostMapping("/trigger/youth")
    public String triggerYouth() {
        adminPolicyService.triggerFetchYouthPolicies();
        return "redirect:/admin/policies";
    }

    @PostMapping("/trigger/public")
    public String triggerPublic() {
        adminPolicyService.triggerFetchPublicServices();
        return "redirect:/admin/policies";
    }
}
