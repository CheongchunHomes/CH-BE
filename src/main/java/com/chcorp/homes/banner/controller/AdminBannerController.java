package com.chcorp.homes.banner.controller;

import com.chcorp.homes.banner.dto.request.BannerRequestDto;
import com.chcorp.homes.banner.dto.response.BannerResponseDto;
import com.chcorp.homes.banner.service.BannerService;
import com.chcorp.homes.notice.repository.NoticeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/banners")
public class AdminBannerController {

    private final BannerService bannerService;
    private final NoticeRepository noticeRepository;

    // 배너 목록 화면
    @GetMapping
    public String list(Model model) {
        model.addAttribute("banners", bannerService.getAllBannersForAdmin());
        return "admin/banners/list";
    }

    // 배너 목록 화면 (alias)
    @GetMapping("/list")
    public String listAlias(Model model) {
        model.addAttribute("banners", bannerService.getAllBannersForAdmin());
        return "admin/banners/list";
    }

    // 공지 목록 + 이미 배너 연결된 noticeId 목록 model에 세팅
    // 드롭다운에서 [배너연결] 표시용
    private void addNotices(Model model) {
        model.addAttribute("notices",
                noticeRepository.findByCategoryNotOrderByNoticeIdDesc("커뮤니티"));
        model.addAttribute("usedNoticeIds",
                bannerService.getUsedNoticeIds());
    }

    // 배너 등록 폼
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("dto", new BannerRequestDto());
        model.addAttribute("isEdit", false);
        addNotices(model);
        return "admin/banners/form";
    }

    // 배너 등록 처리
    @PostMapping("/new")
    public String createBanner(@Valid @ModelAttribute("dto") BannerRequestDto dto,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            addNotices(model);
            return "admin/banners/form";
        }
        bannerService.createBanner(dto);
        return "redirect:/admin/banners/list";
    }

    // 배너 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        BannerResponseDto banner = bannerService.getOne(id);

        // 기존 배너 데이터를 BannerRequestDto에 세팅해서 폼에 바인딩
        BannerRequestDto dto = new BannerRequestDto();
        dto.setTitle(banner.title());
        dto.setContent(banner.content());
        dto.setNoticeId(banner.noticeId());
        dto.setStartDate(banner.startDate());
        dto.setEndDate(banner.endDate());
        dto.setSortOrder(banner.sortOrder());
        dto.setVisible(banner.isVisible());

        model.addAttribute("dto", dto);
        model.addAttribute("bannerId", id);
        model.addAttribute("isEdit", true);
        // 수정 폼: 자기 자신 noticeId는 usedNoticeIds에서 제외
        model.addAttribute("notices",
                noticeRepository.findByCategoryNotOrderByNoticeIdDesc("커뮤니티"));
        model.addAttribute("usedNoticeIds",
                bannerService.getUsedNoticeIds().stream()
                        .filter(noticeId -> !noticeId.equals(banner.noticeId()))
                        .toList());

        return "admin/banners/form";
    }

    // 배너 수정 처리
    @PostMapping("/{id}/edit")
    public String updateBanner(@PathVariable Long id,
                               @Valid @ModelAttribute("dto") BannerRequestDto dto,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("bannerId", id);
            model.addAttribute("isEdit", true);
            addNotices(model);
            return "admin/banners/form";
        }
        bannerService.updateBanner(id, dto);
        return "redirect:/admin/banners/list";
    }

    // 배너 삭제
    @PostMapping("/{id}/delete")
    public String deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return "redirect:/admin/banners/list";
    }

    // 배너 노출 상태 토글
    @PostMapping("/{id}/toggle")
    public String toggleBanner(@PathVariable Long id) {
        bannerService.toggleBannerVisibility(id);
        return "redirect:/admin/banners/list";
    }
}