package com.chcorp.homes.loans.controller;

import com.chcorp.homes.loans.dto.request.LoanApplicationCreateRequestDTO;
import com.chcorp.homes.loans.dto.response.LoanApplicationResponseDTO;
import com.chcorp.homes.loans.dto.response.LoanApplicationSummaryResponseDTO;
import com.chcorp.homes.loans.service.LoanApplicationService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loan-applications")
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;

    @GetMapping("/me")
    public ResponseEntity<LoanApplicationSummaryResponseDTO> getMyLatestLoanApplication(Authentication authentication) {
        Long userId = resolveUserId(authentication);
        if (userId == null) {
            return ResponseEntity.ok(LoanApplicationSummaryResponseDTO.empty());
        }

        return ResponseEntity.ok(loanApplicationService.getLatestLoanApplication(userId));
    }

    @PostMapping
    public ResponseEntity<?> createLoanApplication(
            Authentication authentication,
            @RequestBody LoanApplicationCreateRequestDTO request
    ) {
        Long userId = resolveUserId(authentication);
        if (userId == null) {
            userId = request.userId();
        }

        try {
            LoanApplicationResponseDTO response = loanApplicationService.createLoanApplication(userId, request);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("message", ex.getReason()));
        }
    }

    private Long resolveUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return null;
        }

        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}