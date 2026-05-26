package com.chcorp.homes.notice.controller;

import com.chcorp.homes.notice.dto.NoticeCreateRequestDTO;
import com.chcorp.homes.notice.dto.NoticeResponseDTO;
import com.chcorp.homes.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NoticeAdminController {

    private final NoticeService noticeService;

    @PostMapping("/admin/notice/write")
    public String createNotice(NoticeCreateRequestDTO request) {
        noticeService.createNoticeFromAdmin(request);

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
            NoticeCreateRequestDTO request
    ) {
        noticeService.updateNoticeFromAdmin(noticeId, request);

        return "redirect:/admin?section=notice&updated=true";
    }

    @PostMapping("/admin/notice/{noticeId}/delete")
    public String deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNoticeFromAdmin(noticeId);

        return "redirect:/admin?section=notice&deleted=true";
    }
}