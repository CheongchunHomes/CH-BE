package com.chcorp.homes.ledger.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenditures")
@Getter @Setter
public class Ledger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expenditure_id")
    @JsonProperty("expenditure_id")
    private Long expenditureId;

    @Column(name = "user_id", nullable = false)
    @JsonProperty("user_id")
    private Long userId;

    private String category;
    private Long amount;
    private String method; // 카드, 현금, 쿠폰
    private String memo;

    @Column(name = "spent_at")
    @JsonProperty("spent_at")
    private LocalDate spentAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}