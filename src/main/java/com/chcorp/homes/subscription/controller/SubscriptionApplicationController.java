package com.chcorp.homes.subscription.controller;

import com.chcorp.homes.subscription.dto.request.SubscriptionApplicationCreateRequestDTO;
import com.chcorp.homes.subscription.dto.response.SubscriptionApplicationLatestResponseDTO;
import com.chcorp.homes.subscription.dto.response.SubscriptionApplicationResponseDTO;
import com.chcorp.homes.subscription.service.SubscriptionApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscription/applications")
@RequiredArgsConstructor
public class SubscriptionApplicationController {

    private final SubscriptionApplicationService subscriptionApplicationService;

    @GetMapping("/me")
    public ResponseEntity<SubscriptionApplicationLatestResponseDTO> getMyLatestApplication(
            Authentication authentication
    ) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }

        Long currentUserId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(subscriptionApplicationService.getLatestApplication(currentUserId));
    }

    @PostMapping
    public ResponseEntity<SubscriptionApplicationResponseDTO> apply(
            Authentication authentication,
            @RequestBody SubscriptionApplicationCreateRequestDTO request
    ) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }

        Long currentUserId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(subscriptionApplicationService.apply(currentUserId, request));
    }
}
