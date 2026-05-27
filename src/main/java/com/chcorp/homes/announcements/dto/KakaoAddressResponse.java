package com.chcorp.homes.announcements.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KakaoAddressResponse {

    private List<Document> documents;

    @Getter
    @Setter
    public static class Document {
        private String address_name;

        // x = 경도 longitude
        private String x;

        // y = 위도 latitude
        private String y;
    }
}