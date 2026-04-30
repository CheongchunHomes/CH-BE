package com.chcorp.homes.users.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    private Long userId;
    private LocalDate birthDate;
    private  Boolean isMarried;
    private Boolean isHouseless;
    private String currentResidence;
    private  Long annualIncome;
    private Integer subscriptionMonths;
    private String desiredCity;
    private Boolean disabilityYn;
    private Integer dependentCount;
}
