package com.chcorp.homes.ledger.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "ledger")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Ledger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expenditure_id")
    private Long expenditureId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false, length = 30)
    private String method;

    @Column(length = 255)
    private String memo;

    @Column(name = "spent_at", nullable = false)
    private LocalDate spentAt;

    public void update(String category, Long amount, String method, String memo, LocalDate spentAt) {
        this.category = category;
        this.amount = amount;
        this.method = method;
        this.memo = memo;
        this.spentAt = spentAt;
    }
}