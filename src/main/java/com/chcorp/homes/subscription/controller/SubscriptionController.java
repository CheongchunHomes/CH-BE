package com.chcorp.homes.subscription.controller;

import com.chcorp.homes.subscription.dto.SubscriptionDTO;
import com.chcorp.homes.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/announcements")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping
    public List<SubscriptionDTO> getAnnouncements(
            @RequestParam(required = false) String recruitmentType
    ) {
        return subscriptionService.getAnnouncements(recruitmentType);
    }
}