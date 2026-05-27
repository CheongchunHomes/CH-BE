package com.chcorp.homes.admin.controller;


import com.chcorp.homes.admin.entity.AdminSubscriptionApplication;
import com.chcorp.homes.admin.repository.AdminSubscriptionResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        List<AdminSubscriptionApplication> result;

        if(userId != null && status != null && !status.isBlank()) {
            result = subscriptionApplicationRepository.findByUserIdAndStatus(userId, status);
        }else if(userId != null) {
            result = subscriptionApplicationRepository.findByUserId(userId);
        }else if(status != null) {
            result = subscriptionApplicationRepository.findByStatus(status);
        }else {
            result = subscriptionApplicationRepository.findAll();
        }

        model.addAttribute("applications", result);
        return "admin/subscription-result/list";
    }
}
