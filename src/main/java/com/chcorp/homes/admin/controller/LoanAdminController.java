package com.chcorp.homes.admin.controller;

import com.chcorp.homes.announcements.repository.AnnouncementRepository;
import com.chcorp.homes.loans.entity.LoanApplication;
import com.chcorp.homes.loans.entity.LoanApplicationStatus;
import com.chcorp.homes.loans.entity.LoanProduct;
import com.chcorp.homes.loans.repository.LoanApplicationRepository;
import com.chcorp.homes.loans.repository.LoanProductRepository;
import com.chcorp.homes.policies.repository.PolicyRepository;
import com.chcorp.homes.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class LoanAdminController {

    private final UserRepository userRepository;
    private final AnnouncementRepository announcementRepository;
    private final PolicyRepository policyRepository;
    private final LoanProductRepository loanProductRepository;
    private final LoanApplicationRepository loanApplicationRepository;

    @GetMapping("/loan")
    public String loanHub(Model model) {
        model.addAttribute("currentMenu", "loan");
        model.addAttribute("registeredUserCount", userRepository.count());
        model.addAttribute("productCount", loanProductRepository.count());
        model.addAttribute("applicationCount", loanApplicationRepository.count());
        return "admin-loan";
    }

    @GetMapping("/loan-products")
    public String loanProducts(Model model) {
        List<LoanProduct> products = loanProductRepository.findAll(Sort.by(Sort.Direction.DESC, "loanId"));
        model.addAttribute("currentMenu", "loan");
        model.addAttribute("products", products);
        model.addAttribute("productCount", products.size());
        return "admin-loan-products";
    }

    @GetMapping("/loan-products/{loanId}/edit")
    public String editLoanProduct(@PathVariable Long loanId, Model model) {
        LoanProduct product = loanProductRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan product not found: " + loanId));

        model.addAttribute("currentMenu", "loan");
        model.addAttribute("product", product);
        return "admin-loan-product-edit";
    }

    @PostMapping("/loan-products")
    public String createLoanProduct(
            @RequestParam String externalCode,
            @RequestParam String provider,
            @RequestParam String name,
            @RequestParam String loanType,
            @RequestParam(required = false) BigDecimal interestRate,
            @RequestParam(required = false) BigDecimal interestRateMin,
            @RequestParam(required = false) Long maxAmount,
            @RequestParam(required = false) Long incomeLimit,
            @RequestParam(required = false) String conditions,
            @RequestParam(defaultValue = "false") boolean policyLoan,
            @RequestParam(defaultValue = "true") boolean visible
    ) {
        LoanProduct product = LoanProduct.builder()
                .externalCode(externalCode)
                .provider(provider)
                .name(name)
                .loanType(loanType)
                .interestRate(interestRate)
                .interestRateMin(interestRateMin)
                .maxAmount(maxAmount)
                .incomeLimit(incomeLimit)
                .conditions(conditions)
                .policyLoan(policyLoan)
                .visible(visible)
                .syncedAt(Instant.now())
                .build();

        loanProductRepository.save(product);
        return "redirect:/admin/loan-products";
    }

    @PostMapping("/loan-products/{loanId}/edit")
    public String updateLoanProduct(
            @PathVariable Long loanId,
            @RequestParam String externalCode,
            @RequestParam String provider,
            @RequestParam String name,
            @RequestParam String loanType,
            @RequestParam(required = false) BigDecimal interestRate,
            @RequestParam(required = false) BigDecimal interestRateMin,
            @RequestParam(required = false) Long maxAmount,
            @RequestParam(required = false) Long incomeLimit,
            @RequestParam(required = false) String conditions,
            @RequestParam(defaultValue = "false") boolean policyLoan,
            @RequestParam(defaultValue = "true") boolean visible
    ) {
        LoanProduct product = loanProductRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan product not found: " + loanId));

        product.setExternalCode(externalCode);
        product.setProvider(provider);
        product.setName(name);
        product.setLoanType(loanType);
        product.setInterestRate(interestRate);
        product.setInterestRateMin(interestRateMin);
        product.setMaxAmount(maxAmount);
        product.setIncomeLimit(incomeLimit);
        product.setConditions(conditions);
        product.setPolicyLoan(policyLoan);
        product.setVisible(visible);
        product.setSyncedAt(Instant.now());

        loanProductRepository.save(product);
        return "redirect:/admin/loan-products";
    }

    @GetMapping("/loan-applications")
    public String loanApplications(Model model) {
        List<LoanApplication> applications = loanApplicationRepository.findAllWithUserAndLoanProduct();
        model.addAttribute("currentMenu", "loan");
        model.addAttribute("applications", applications);
        model.addAttribute("statusOptions", LoanApplicationStatus.values());
        model.addAttribute("applicationCount", applications.size());
        return "admin-loan-applications";
    }

    @PostMapping("/loan-applications/{applicationId}/status")
    public String updateLoanApplicationStatus(
            @PathVariable Long applicationId,
            @RequestParam String status
    ) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Loan application not found: " + applicationId));

        LoanApplicationStatus nextStatus = LoanApplicationStatus.valueOf(status);
        application.setStatus(nextStatus);

        if (nextStatus == LoanApplicationStatus.PAYMENT_APPROVED
                || nextStatus == LoanApplicationStatus.PAYMENT_REJECTED) {
            application.setDecisionAt(Instant.now());
        } else {
            application.setDecisionAt(null);
        }

        loanApplicationRepository.save(application);
        return "redirect:/admin/loan-applications";
    }

}
