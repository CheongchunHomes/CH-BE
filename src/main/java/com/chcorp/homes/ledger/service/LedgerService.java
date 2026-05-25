package com.chcorp.homes.ledger.service;

import com.chcorp.homes.ledger.dto.LedgerRequestDTO;
import com.chcorp.homes.ledger.dto.LedgerResponseDTO;
import com.chcorp.homes.ledger.entity.Ledger;
import com.chcorp.homes.ledger.repository.LedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LedgerService {

    private final LedgerRepository ledgerRepository;

    public List<LedgerResponseDTO> getLedgers(Long userId, String month, String category) {
        if (month == null || month.isBlank()) {
            return ledgerRepository.findByUserIdOrderBySpentAtDescExpenditureIdDesc(userId)
                    .stream()
                    .map(LedgerResponseDTO::from)
                    .toList();
        }

        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        boolean hasCategory = category != null
                && !category.isBlank()
                && !"전체".equals(category);

        List<Ledger> ledgers = hasCategory
                ? ledgerRepository.findByUserIdAndCategoryAndSpentAtBetweenOrderBySpentAtDescExpenditureIdDesc(
                userId,
                category,
                startDate,
                endDate
        )
                : ledgerRepository.findByUserIdAndSpentAtBetweenOrderBySpentAtDescExpenditureIdDesc(
                userId,
                startDate,
                endDate
        );

        return ledgers.stream()
                .map(LedgerResponseDTO::from)
                .toList();
    }

    public LedgerResponseDTO getLedger(Long expenditureId, Long userId) {
        Ledger ledger = findMyLedger(expenditureId, userId);
        return LedgerResponseDTO.from(ledger);
    }

    @Transactional
    public LedgerResponseDTO createLedger(Long userId, LedgerRequestDTO request) {
        Ledger ledger = Ledger.builder()
                .userId(userId)
                .category(request.category())
                .amount(request.amount())
                .method(request.method())
                .memo(request.memo())
                .spentAt(request.spentAt())
                .build();

        Ledger savedLedger = ledgerRepository.save(ledger);

        return LedgerResponseDTO.from(savedLedger);
    }

    @Transactional
    public LedgerResponseDTO updateLedger(Long expenditureId, Long userId, LedgerRequestDTO request) {
        Ledger ledger = findMyLedger(expenditureId, userId);

        ledger.update(
                request.category(),
                request.amount(),
                request.method(),
                request.memo(),
                request.spentAt()
        );

        return LedgerResponseDTO.from(ledger);
    }

    @Transactional
    public void deleteLedger(Long expenditureId, Long userId) {
        Ledger ledger = findMyLedger(expenditureId, userId);
        ledgerRepository.delete(ledger);
    }

    private Ledger findMyLedger(Long expenditureId, Long userId) {
        Ledger ledger = ledgerRepository.findById(expenditureId)
                .orElseThrow(() -> new IllegalArgumentException("가계부 내역을 찾을 수 없습니다."));

        if (!ledger.getUserId().equals(userId)) {
            throw new IllegalArgumentException("해당 가계부 내역에 접근할 권한이 없습니다.");
        }

        return ledger;
    }
}