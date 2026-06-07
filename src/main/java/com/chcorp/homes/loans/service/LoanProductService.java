package com.chcorp.homes.loans.service;

import com.chcorp.homes.loans.dto.response.LoanProductResponseDTO;
import com.chcorp.homes.loans.repository.LoanProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanProductService {

    private final LoanProductRepository loanProductRepository;

    public List<LoanProductResponseDTO> listVisibleLoanProducts() {
        return loanProductRepository.findAll(Sort.by(Sort.Direction.DESC, "loanId")).stream()
                .filter(product -> Boolean.TRUE.equals(product.getVisible()))
                .map(LoanProductResponseDTO::from)
                .toList();
    }
}