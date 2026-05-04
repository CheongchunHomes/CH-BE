package com.chcorp.homes.ledger.controller;

import com.chcorp.homes.ledger.entity.Ledger;
import com.chcorp.homes.ledger.repository.LedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerRepository ledgerRepository;

    @PostMapping("/add")
    public Ledger addEntry(@RequestBody Ledger ledger) {
        if (ledger.getUserId() == null) {
            ledger.setUserId(1L);
        }

        if (ledger.getSpentAt() == null) {
            ledger.setSpentAt(LocalDate.now());
        }

        return ledgerRepository.save(ledger);
    }

    @GetMapping("/list")
    public List<Ledger> getEntries(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            return ledgerRepository.findByUserIdOrderBySpentAtDesc(userId);
        }

        return ledgerRepository.findAll();
    }
}