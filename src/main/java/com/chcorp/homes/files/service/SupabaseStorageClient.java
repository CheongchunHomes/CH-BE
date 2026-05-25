package com.chcorp.homes.files.service;

import com.chcorp.homes.common.config.SupabaseStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SupabaseStorageClient {

    private final RestTemplate restTemplate;
    private final SupabaseStorageProperties properties;

    public String createSignedUploadUrl(String objectPath) {
        Map<?, ?> response = exchange(
                HttpMethod.POST,
                "/object/upload/sign/" + bucket() + "/" + objectPath,
                Map.of(),
                Map.class
        );

        Object signedUrl = firstPresent(response, "signedUrl", "signedURL", "url");
        if (signedUrl != null) {
            return qualifyUrl(signedUrl.toString());
        }

        Object token = response.get("token");
        if (token != null) {
            return storageBaseUrl() + "/object/upload/sign/" + bucket() + "/" + objectPath
                    + "?token=" + token;
        }

        throw new IllegalStateException("Supabase signed upload URL 응답이 올바르지 않습니다.");
    }

    public String createSignedDownloadUrl(String objectPath) {
        Map<String, Object> body = Map.of(
                "expiresIn",
                properties.storage().signedDownloadTtlSeconds()
        );

        Map<?, ?> response = exchange(
                HttpMethod.POST,
                "/object/sign/" + bucket() + "/" + objectPath,
                body,
                Map.class
        );

        Object signedUrl = firstPresent(response, "signedUrl", "signedURL");
        if (signedUrl == null) {
            throw new IllegalStateException("Supabase signed download URL 응답이 올바르지 않습니다.");
        }

        return qualifyUrl(signedUrl.toString());
    }

    public boolean exists(String objectPath) {
        try {
            restTemplate.exchange(
                    storageBaseUrl() + "/object/" + bucket() + "/" + objectPath,
                    HttpMethod.HEAD,
                    new HttpEntity<>(headers()),
                    Void.class
            );
            return true;
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().value() == 404) {
                return false;
            }
            throw e;
        }
    }

    public void delete(String objectPath) {
        Map<String, Object> body = Map.of("prefixes", List.of(objectPath));

        exchange(
                HttpMethod.DELETE,
                "/object/" + bucket(),
                body,
                Object.class
        );
    }

    public long signedDownloadTtlSeconds() {
        return properties.storage().signedDownloadTtlSeconds();
    }

    private <T> T exchange(HttpMethod method, String path, Object body, Class<T> responseType) {
        ResponseEntity<T> response = restTemplate.exchange(
                storageBaseUrl() + path,
                method,
                new HttpEntity<>(body, headers()),
                responseType
        );

        T responseBody = response.getBody();
        if (responseBody == null) {
            throw new IllegalStateException("Supabase Storage 응답이 비어 있습니다.");
        }

        return responseBody;
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", properties.secretKey());
        headers.setBearerAuth(properties.secretKey());
        return headers;
    }

    private String storageBaseUrl() {
        return properties.url().replaceAll("/+$", "") + "/storage/v1";
    }

    private String bucket() {
        return properties.storage().privateFileBucket();
    }

    private String qualifyUrl(String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }

        if (url.startsWith("/storage/v1/")) {
            return properties.url().replaceAll("/+$", "") + url;
        }

        if (url.startsWith("/")) {
            return storageBaseUrl() + url;
        }

        return storageBaseUrl() + "/" + url;
    }

    private Object firstPresent(Map<?, ?> response, String... keys) {
        for (String key : keys) {
            Object value = response.get(key);
            if (value != null) {
                return value;
            }
        }

        return null;
    }
}
