package com.chcorp.homes.subscription.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubscriptionHouseTypeImportResultDTO {

    private int totalApiCount;
    private int matchedAnnouncementCount;
    private int savedCount;
    private int skippedCount;
}