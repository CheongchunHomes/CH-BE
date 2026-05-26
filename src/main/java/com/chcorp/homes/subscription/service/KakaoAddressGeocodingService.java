package com.chcorp.homes.subscription.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * 카카오 주소 검색 API를 사용해서 주소를 위도/경도로 변환하는 서비스입니다.
 */
@Service
public class KakaoAddressGeocodingService {

    private static final String KAKAO_ADDRESS_SEARCH_URL =
            "https://dapi.kakao.com/v2/local/search/address.json";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Value("${api.kakao.rest-api-key:}")
    private String kakaoRestApiKey;

    /**
     * 주소 문자열을 위도/경도로 변환합니다.
     */
    public Optional<Coordinates> geocode(String address) {
        if (address == null || address.isBlank()) {
            return Optional.empty();
        }

        if (kakaoRestApiKey == null || kakaoRestApiKey.isBlank()) {
            throw new IllegalStateException("KAKAO_REST_API_KEY 환경변수가 설정되지 않았습니다.");
        }

        try {
            String encodedAddress = URLEncoder.encode(address.trim(), StandardCharsets.UTF_8);
            URI uri = URI.create(KAKAO_ADDRESS_SEARCH_URL + "?query=" + encodedAddress);

            HttpRequest request = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(8))
                    .header("Authorization", "KakaoAK " + kakaoRestApiKey)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return Optional.empty();
            }

            KakaoAddressSearchResponse payload = objectMapper.readValue(
                    response.body(),
                    KakaoAddressSearchResponse.class
            );

            if (payload.documents() == null || payload.documents().isEmpty()) {
                return Optional.empty();
            }

            KakaoAddressDocument firstDocument = payload.documents().get(0);

            if (firstDocument.x() == null || firstDocument.y() == null) {
                return Optional.empty();
            }

            return Optional.of(new Coordinates(
                    new BigDecimal(firstDocument.y()),
                    new BigDecimal(firstDocument.x())
            ));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * 지도 좌표 값입니다.
     * latitude = 위도, longitude = 경도입니다.
     */
    public record Coordinates(
            BigDecimal latitude,
            BigDecimal longitude
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record KakaoAddressSearchResponse(
            List<KakaoAddressDocument> documents
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record KakaoAddressDocument(
            String x,
            String y
    ) {
    }
}