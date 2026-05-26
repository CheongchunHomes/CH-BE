package com.chcorp.homes.admin.controller;


import com.chcorp.homes.admin.repository.AdminSubscriptionResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/subscription-result")
@RequiredArgsConstructor
public class SubscriptionResultController {

    private final AdminSubscriptionResultRepository subscriptionApplicationRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("applications", subscriptionApplicationRepository.findAll());
        return "admin/subscription-result/list";
    }

    @PostMapping("/{id}/result")
    public String updateResult(@PathVariable Long id, @RequestParam String status) {
        subscriptionApplicationRepository.findById(id).ifPresent(app -> {
        app.updateResult(status, java.time.LocalDateTime.now());
        subscriptionApplicationRepository.save(app);
    });
    return"redirect:/admin/subscription-result";

    }

}
