package com.chcorp.homes.loans.controller;

import com.chcorp.homes.loans.dto.response.LoanProductResponseDTO;
import com.chcorp.homes.loans.service.LoanProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loan-products")
public class LoanProductApiController {

    private final LoanProductService loanProductService;

    @GetMapping
    public List<LoanProductResponseDTO> list() {
        return loanProductService.listVisibleLoanProducts();
    }
}