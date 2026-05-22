package com.chcorp.homes.notice.controller;

import com.chcorp.homes.notice.dto.NoticeCreateRequestDTO;
import com.chcorp.homes.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class NoticeAdminController {

    private final NoticeService noticeService;

    @PostMapping("/admin/notice/write")
    public String createNotice(NoticeCreateRequestDTO request) {
        noticeService.createNoticeFromAdmin(request);

        return "redirect:/admin?section=notice&saved=true";
    }
}