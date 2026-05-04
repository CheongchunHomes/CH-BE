package com.chcorp.homes.recommend_test.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recoentity")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Recoentity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; //정책명
    private String category; //카테고리 (청약,전월세 등)
    private int minAge; //최소나이
    private int maxAge; //최대나이
    private int maxIncome; //소득기준
    private String region; //지역
    private String description; //설명
    private String applyUrl; //신청 링크
    @Column(name = "is_active")
    private boolean active; //현재 신청 가능 여부



}
