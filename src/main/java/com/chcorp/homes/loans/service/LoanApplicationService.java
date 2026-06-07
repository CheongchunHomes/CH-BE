package com.chcorp.homes.loans.service;

import com.chcorp.homes.loans.dto.request.LoanApplicationCreateRequestDTO;
import com.chcorp.homes.loans.dto.response.LoanApplicationResponseDTO;
import com.chcorp.homes.loans.dto.response.LoanApplicationSummaryResponseDTO;
import com.chcorp.homes.loans.entity.LoanApplication;
import com.chcorp.homes.loans.entity.LoanApplicationStatus;
import com.chcorp.homes.loans.entity.LoanProduct;
import com.chcorp.homes.loans.repository.LoanApplicationRepository;
import com.chcorp.homes.loans.repository.LoanProductRepository;
import com.chcorp.homes.users.entity.User;
import com.chcorp.homes.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class LoanApplicationService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanProductRepository loanProductRepository;
    private final UserRepository userRepository;

    @Transactional
    public LoanApplicationResponseDTO createLoanApplication(Long userId, LoanApplicationCreateRequestDTO request) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        if (request.loanId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "loanId가 필요합니다.");
        }
        if (request.applyAmount() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "applyAmount가 필요합니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        LoanProduct loanProduct = loanProductRepository.findById(request.loanId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "대출 상품을 찾을 수 없습니다."));

        LoanApplication application = LoanApplication.builder()
                .user(user)
                .loanProduct(loanProduct)
                .applyAmount(request.applyAmount())
                .address(request.address())
                .status(LoanApplicationStatus.PAYMENT_PENDING)
                .build();

        return LoanApplicationResponseDTO.from(loanApplicationRepository.save(application));
    }

    public LoanApplicationSummaryResponseDTO getLatestLoanApplication(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return LoanApplicationSummaryResponseDTO.empty();
        }

        LoanApplication application = loanApplicationRepository
                .findFirstByUserOrderByUpdatedAtDescApplicationIdDesc(user)
                .orElse(null);

        if (application == null) {
            return LoanApplicationSummaryResponseDTO.empty();
        }

        return LoanApplicationSummaryResponseDTO.from(application);
    }
}