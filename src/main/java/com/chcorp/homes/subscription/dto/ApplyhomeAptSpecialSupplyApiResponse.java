package com.chcorp.homes.subscription.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplyhomeAptSpecialSupplyApiResponse {

    @JsonProperty("data")
    private List<Map<String, Object>> data;

    @JsonProperty("totalCount")
    private int totalCount;
}