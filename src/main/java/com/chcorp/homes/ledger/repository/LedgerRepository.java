package com.chcorp.homes.ledger.repository; // chcorp 확인

import com.chcorp.homes.ledger.entity.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {
    List<Ledger> findByUserIdOrderBySpentAtDesc(Long userId);
}