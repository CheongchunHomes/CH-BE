package com.chcorp.homes.announcements.controller;

import com.chcorp.homes.announcements.dto.AdminAnnouncementRequestDTO;
import com.chcorp.homes.announcements.entity.Announcement;
import com.chcorp.homes.announcements.service.AdminAnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/announcements")
public class AdminAnnouncementController {

    private final AdminAnnouncementService adminAnnouncementService;

    // =========================
    // 목록
    // =========================
    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "") String region,
                       @RequestParam(defaultValue = "") String status,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {

        Page<Announcement> result = adminAnnouncementService
                .getList(keyword, region, status, page, 10);

        model.addAttribute("announcements", result);
        model.addAttribute("keyword", keyword);
        model.addAttribute("region", region);
        model.addAttribute("status", status);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", result.getTotalPages());

        return "admin/announcements/list";
    }

    // =========================
    // 등록 폼
    // =========================
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new AdminAnnouncementRequestDTO());
        model.addAttribute("isEdit", false);
        return "admin/announcements/form";
    }

    // =========================
    // 등록 처리
    // =========================
    @PostMapping
    public String create(@ModelAttribute("form") AdminAnnouncementRequestDTO dto) {
        adminAnnouncementService.register(dto);
        return "redirect:/admin/announcements";
    }

    // =========================
    // 수정 폼
    // =========================
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Announcement announcement = adminAnnouncementService.getOne(id);

        AdminAnnouncementRequestDTO form = new AdminAnnouncementRequestDTO();
        form.setTitle(announcement.getTitle());
        form.setRegion(announcement.getRegion());
        form.setRecuitmentType(announcement.getRecuitmentType());
        form.setTargetType(announcement.getTargetType());
        form.setStatus(announcement.getStatus());
        form.setAddress(announcement.getAddress());
        form.setContent(announcement.getContent());
        form.setSourceUrl(announcement.getSourceUrl());
        form.setIsVisible(announcement.getIsVisible());
        form.setApplyStartDate(announcement.getApplyStartDate());
        form.setApplyEndDate(announcement.getApplyEndDate());

        model.addAttribute("form", form);
        model.addAttribute("isEdit", true);
        model.addAttribute("announcementId", id);
        return "admin/announcements/form";
    }

    // =========================
    // 수정 처리
    // =========================
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute("form") AdminAnnouncementRequestDTO dto) {
        adminAnnouncementService.update(id, dto);
        return "redirect:/admin/announcements";
    }

    // =========================
    // 노출 여부 변경
    // =========================
    @PostMapping("/{id}/visibility")
    public String updateVisibility(@PathVariable Long id,
                                   @RequestParam Boolean isVisible) {
        adminAnnouncementService.updateVisibility(id, isVisible);
        return "redirect:/admin/announcements";
    }

    // =========================
    // 삭제
    // =========================
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        adminAnnouncementService.delete(id);
        return "redirect:/admin/announcements";
    }

    // =========================
    // 외부 API 트리거
    // =========================
    @PostMapping("/trigger/regions")
    public String triggerRegions() {
        adminAnnouncementService.triggerFetchAllRegions();
        return "redirect:/admin/announcements";
    }

    @PostMapping("/trigger/sale")
    public String triggerSale() {
        adminAnnouncementService.triggerFetchSale();
        return "redirect:/admin/announcements";
    }

    @PostMapping("/trigger/applyhome")
    public String triggerApplyhome() {
        adminAnnouncementService.triggerFetchApplyhome();
        return "redirect:/admin/announcements";
    }
}
