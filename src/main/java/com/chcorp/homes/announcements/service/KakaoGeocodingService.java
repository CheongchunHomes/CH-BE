package com.chcorp.homes.announcements.service;

import com.chcorp.homes.announcements.dto.KakaoAddressResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoGeocodingService {

    private final RestTemplate restTemplate;

    @Value("${kakao.rest-api-key}")
    private String kakaoRestApiKey;

    private static final String KAKAO_ADDRESS_SEARCH_URL =
            "https://dapi.kakao.com/v2/local/search/address.json";

    public Coordinate getCoordinateByAddress(String address) {
        if (address == null || address.isBlank()) {
            return null;
        }

        try {
            URI uri = UriComponentsBuilder
                    .fromUriString(KAKAO_ADDRESS_SEARCH_URL)
                    .queryParam("query", address)
                    .build()
                    .encode()
                    .toUri();

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<KakaoAddressResponse> response =
                    restTemplate.exchange(
                            uri,
                            HttpMethod.GET,
                            entity,
                            KakaoAddressResponse.class
                    );

            KakaoAddressResponse body = response.getBody();

            if (body == null
                    || body.getDocuments() == null
                    || body.getDocuments().isEmpty()) {
                log.warn("[카카오 주소검색] 좌표 검색 결과 없음 - address={}", address);
                return null;
            }

            KakaoAddressResponse.Document document = body.getDocuments().get(0);

            BigDecimal longitude = new BigDecimal(document.getX());
            BigDecimal latitude = new BigDecimal(document.getY());

            return new Coordinate(latitude, longitude);

        } catch (Exception e) {
            log.warn(
                    "[카카오 주소검색] 좌표 변환 실패 - address={}, message={}",
                    address,
                    e.getMessage()
            );
            return null;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Coordinate {
        private BigDecimal latitude;
        private BigDecimal longitude;
    }
}