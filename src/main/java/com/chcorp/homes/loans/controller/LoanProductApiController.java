package com.chcorp.homes.loans.controller;

import com.chcorp.homes.loans.entity.LoanProduct;
import com.chcorp.homes.loans.repository.LoanProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loan-products")
public class LoanProductApiController {

    private final LoanProductRepository loanProductRepository;

    @GetMapping
    public List<LoanProduct> list() {
        return loanProductRepository.findAll(Sort.by(Sort.Direction.DESC, "loanId")).stream()
                .filter(product -> Boolean.TRUE.equals(product.getVisible()))
                .collect(Collectors.toList());
    }
}
