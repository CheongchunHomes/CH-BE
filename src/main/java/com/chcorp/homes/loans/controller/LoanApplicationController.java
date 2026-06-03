package com.chcorp.homes.loans.controller;

import com.chcorp.homes.loans.entity.LoanApplication;
import com.chcorp.homes.loans.repository.LoanApplicationRepository;
import com.chcorp.homes.users.entity.User;
import com.chcorp.homes.users.repository.UserRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loan-applications")
public class LoanApplicationController {

    private final LoanApplicationRepository loanApplicationRepository;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<LoanApplicationSummaryResponse> getMyLatestLoanApplication(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.ok(LoanApplicationSummaryResponse.empty());
        }

        Long userId;
        try {
            userId = Long.valueOf(authentication.getName());
        } catch (NumberFormatException ex) {
            return ResponseEntity.ok(LoanApplicationSummaryResponse.empty());
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.ok(LoanApplicationSummaryResponse.empty());
        }

        LoanApplication application = loanApplicationRepository.findFirstByUserOrderByUpdatedAtDescApplicationIdDesc(user)
                .orElse(null);

        if (application == null) {
            return ResponseEntity.ok(LoanApplicationSummaryResponse.empty());
        }

        return ResponseEntity.ok(LoanApplicationSummaryResponse.from(application));
    }

    public record LoanApplicationSummaryResponse(
            Long applicationId,
            String status,
            Instant updatedAt,
            Instant createdAt,
            Long loanId,
            Long userId
    ) {
        static LoanApplicationSummaryResponse empty() {
            return new LoanApplicationSummaryResponse(null, null, null, null, null, null);
        }

        static LoanApplicationSummaryResponse from(LoanApplication application) {
            return new LoanApplicationSummaryResponse(
                    application.getApplicationId(),
                    application.getStatus() != null ? application.getStatus().name() : null,
                    application.getUpdatedAt(),
                    application.getCreatedAt(),
                    application.getLoanProduct() != null ? application.getLoanProduct().getLoanId() : null,
                    application.getUser() != null ? application.getUser().getId() : null
            );
        }
    }
}
