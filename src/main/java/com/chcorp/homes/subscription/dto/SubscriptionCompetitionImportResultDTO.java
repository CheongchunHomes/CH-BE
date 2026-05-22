package com.chcorp.homes.subscription.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubscriptionCompetitionImportResultDTO {

    private int totalApiCount;
    private int savedCount;
    private int skippedCount;
    private int failedCount;
}