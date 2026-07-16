package com.chcorp.homes.ledger.repository;

import com.chcorp.homes.ledger.entity.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {

    List<Ledger> findByUserIdOrderBySpentAtDescExpenditureIdDesc(Long userId);

    List<Ledger> findByUserIdAndSpentAtBetweenOrderBySpentAtDescExpenditureIdDesc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Ledger> findByUserIdAndCategoryAndSpentAtBetweenOrderBySpentAtDescExpenditureIdDesc(
            Long userId,
            String category,
            LocalDate startDate,
            LocalDate endDate
    );
}