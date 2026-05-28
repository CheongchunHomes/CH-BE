package com.chcorp.homes.notice.controller;

import com.chcorp.homes.notice.dto.NoticeCreateRequestDTO;
import com.chcorp.homes.notice.dto.NoticeResponseDTO;
import com.chcorp.homes.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NoticeAdminController {

    private final NoticeService noticeService;

    @PostMapping("/admin/notice/write")
    public String createNotice(
            NoticeCreateRequestDTO request,
            @RequestParam(value = "important", required = false) List<String> importantValues
    ) {
        noticeService.createNoticeFromAdmin(normalizeImportant(request, importantValues));

        return "redirect:/admin?section=notice&saved=true";
    }

    @GetMapping("/admin/notice/list")
    @ResponseBody
    public List<NoticeResponseDTO> getNoticesForAdmin() {
        return noticeService.getNotices();
    }

    @PostMapping("/admin/notice/{noticeId}/edit")
    public String updateNotice(
            @PathVariable Long noticeId,
            NoticeCreateRequestDTO request,
            @RequestParam(value = "important", required = false) List<String> importantValues
    ) {
        noticeService.updateNoticeFromAdmin(noticeId, normalizeImportant(request, importantValues));

        return "redirect:/admin?section=notice&updated=true";
    }

    @PostMapping("/admin/notice/{noticeId}/delete")
    public String deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNoticeFromAdmin(noticeId);

        return "redirect:/admin?section=notice&deleted=true";
    }

    @PostMapping("/admin/community/notice/write")
    public String createCommunityNotice(
            NoticeCreateRequestDTO request,
            @RequestParam(value = "important", required = false) List<String> importantValues
    ) {
        noticeService.createCommunityNoticeFromAdmin(normalizeImportant(request, importantValues));

        return "redirect:/admin?section=community&communityNoticeSaved=true";
    }

    @GetMapping("/admin/community/notice/list")
    @ResponseBody
    public List<NoticeResponseDTO> getCommunityNoticesForAdmin() {
        return noticeService.getCommunityNoticesForAdmin();
    }

    @PostMapping("/admin/community/notice/{noticeId}/edit")
    public String updateCommunityNotice(
            @PathVariable Long noticeId,
            NoticeCreateRequestDTO request,
            @RequestParam(value = "important", required = false) List<String> importantValues
    ) {
        noticeService.updateCommunityNoticeFromAdmin(noticeId, normalizeImportant(request, importantValues));

        return "redirect:/admin?section=community&communityNoticeUpdated=true";
    }

    @PostMapping("/admin/community/notice/{noticeId}/delete")
    public String deleteCommunityNotice(@PathVariable Long noticeId) {
        noticeService.deleteCommunityNoticeFromAdmin(noticeId);

        return "redirect:/admin?section=community&communityNoticeDeleted=true";
    }

    private NoticeCreateRequestDTO normalizeImportant(
            NoticeCreateRequestDTO request,
            List<String> importantValues
    ) {
        boolean important = importantValues != null && importantValues.contains("true");

        return new NoticeCreateRequestDTO(
                request.category(),
                request.title(),
                request.summary(),
                request.content(),
                important
        );
    }
}
