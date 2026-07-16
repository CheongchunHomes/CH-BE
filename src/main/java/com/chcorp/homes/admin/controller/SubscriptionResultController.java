package com.chcorp.homes.admin.controller;

import com.chcorp.homes.admin.repository.AdminSubscriptionResultRepository;
import com.chcorp.homes.subscription.entity.SubscriptionApplication;
import com.chcorp.homes.subscription.entity.SubscriptionApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/subscription-result")
@RequiredArgsConstructor
public class SubscriptionResultController {

    private final AdminSubscriptionResultRepository subscriptionApplicationRepository;

    @GetMapping
    public String list(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            Model model
    ) {
        List<SubscriptionApplication> result = fetchApplications(userId, status);

        model.addAttribute("applications", result);
        return "admin/subscription-result/list";
    }

    @PostMapping("/{id}/result")
    public String updateResult(
            @PathVariable("id") Long id,
            @RequestParam("status") String status
    ) {
        SubscriptionApplication application = subscriptionApplicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("청약 신청 내역을 찾을 수 없습니다. id=" + id));

        if (application.getStatus() != SubscriptionApplicationStatus.PENDING) {
            return "redirect:/admin/subscription-result";
        }

        application.applyResult(SubscriptionApplicationStatus.valueOf(status), LocalDateTime.now());
        subscriptionApplicationRepository.save(application);

        return "redirect:/admin/subscription-result";
    }

    private List<SubscriptionApplication> fetchApplications(Long userId, String status) {
        boolean hasUserId = userId != null;
        boolean hasStatus = status != null && !status.isBlank();

        if (hasUserId && hasStatus) {
            return subscriptionApplicationRepository.findByUser_IdAndStatusOrderByIdDesc(
                    userId,
                    SubscriptionApplicationStatus.valueOf(status)
            );
        }

        if (hasUserId) {
            return subscriptionApplicationRepository.findByUser_IdOrderByIdDesc(userId);
        }

        if (hasStatus) {
            return subscriptionApplicationRepository.findByStatusOrderByIdDesc(
                    SubscriptionApplicationStatus.valueOf(status)
            );
        }

        return subscriptionApplicationRepository.findAllByOrderByIdDesc();
    }
}
