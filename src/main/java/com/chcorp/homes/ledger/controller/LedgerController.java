package com.chcorp.homes.ledger.controller;

import com.chcorp.homes.ledger.dto.LedgerRequestDTO;
import com.chcorp.homes.ledger.dto.LedgerResponseDTO;
import com.chcorp.homes.ledger.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ledger")
public class LedgerController {

    private final LedgerService ledgerService;

    @GetMapping
    public ResponseEntity<List<LedgerResponseDTO>> getLedgers(
            Authentication authentication,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String category
    ) {
        Long userId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(
                ledgerService.getLedgers(userId, month, category)
        );
    }

    @GetMapping("/{expenditureId}")
    public ResponseEntity<LedgerResponseDTO> getLedger(
            Authentication authentication,
            @PathVariable Long expenditureId
    ) {
        Long userId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(
                ledgerService.getLedger(expenditureId, userId)
        );
    }

    @PostMapping
    public ResponseEntity<LedgerResponseDTO> createLedger(
            Authentication authentication,
            @RequestBody LedgerRequestDTO request
    ) {
        Long userId = Long.valueOf(authentication.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ledgerService.createLedger(userId, request));
    }

    /**
     * 기존 FE가 /ledger/add를 호출하고 있으면 깨지지 않게 유지.
     */
    @PostMapping("/add")
    public ResponseEntity<LedgerResponseDTO> createLedgerByAddPath(
            Authentication authentication,
            @RequestBody LedgerRequestDTO request
    ) {
        Long userId = Long.valueOf(authentication.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ledgerService.createLedger(userId, request));
    }

    @PutMapping("/{expenditureId}")
    public ResponseEntity<LedgerResponseDTO> updateLedger(
            Authentication authentication,
            @PathVariable Long expenditureId,
            @RequestBody LedgerRequestDTO request
    ) {
        Long userId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(
                ledgerService.updateLedger(expenditureId, userId, request)
        );
    }

    @DeleteMapping("/{expenditureId}")
    public ResponseEntity<Void> deleteLedger(
            Authentication authentication,
            @PathVariable Long expenditureId
    ) {
        Long userId = Long.valueOf(authentication.getName());

        ledgerService.deleteLedger(expenditureId, userId);

        return ResponseEntity.noContent().build();
    }
}