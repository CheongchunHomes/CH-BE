package com.chcorp.homes.users.controller;

import com.chcorp.homes.users.entity.User;
import com.chcorp.homes.users.service.AdminUserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = size > 0 ? size : 10;

        Page<User> users = adminUserService.getUsers(
                keyword,
                status,
                PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "id"))
        );
        int currentPage = safePage;
        int totalPages = users.getTotalPages();

        if (totalPages > 0 && currentPage >= totalPages) {
            currentPage = totalPages - 1;
            users = adminUserService.getUsers(
                    keyword,
                    status,
                    PageRequest.of(currentPage, safeSize, Sort.by(Sort.Direction.DESC, "id"))
            );
            totalPages = users.getTotalPages();
        }

        List<User> adminUsers = adminUserService.getAdminUsers();

        model.addAttribute("users", users);
        model.addAttribute("adminUsers", adminUsers);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageSize", safeSize);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", Math.max(0, currentPage - 2));
        model.addAttribute("endPage", Math.min(Math.max(totalPages - 1, 0), currentPage + 2));
        model.addAttribute("registeredUserCount", adminUserService.countRegisteredUsers());
        model.addAttribute("activeUserCount", adminUserService.countActiveUsers());
        model.addAttribute("disabledUserCount", adminUserService.countDisabledUsers());
        model.addAttribute("adminUserCount", adminUserService.countAdminUsers());

        return "admin/users/list";
    }

    @PostMapping("/{userId}/status")
    public String changeStatus(
            @PathVariable Long userId,
            @RequestParam String status,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String filterStatus,
            @RequestParam(defaultValue = "0") int page
    ) {
        adminUserService.changeUserStatus(userId, status);
        return "redirect:" + buildRedirectUrl(keyword, filterStatus, page);
    }

    private String buildRedirectUrl(String keyword, String filterStatus, int page) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/admin/users")
                .queryParam("page", Math.max(page, 0));

        if (StringUtils.hasText(keyword)) {
            builder.queryParam("keyword", keyword.trim());
        }

        if (StringUtils.hasText(filterStatus)) {
            builder.queryParam("status", filterStatus.trim());
        }

        return builder.encode().toUriString();
    }
}
