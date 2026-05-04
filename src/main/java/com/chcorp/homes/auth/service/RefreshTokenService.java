package com.chcorp.homes.auth.service;

import com.chcorp.homes.auth.dto.RefreshTokenRotationDTO;
import com.chcorp.homes.auth.entity.RefreshToken;
import com.chcorp.homes.auth.repository.RefreshTokenRepository;
import com.chcorp.homes.common.config.JwtProperties;
import com.chcorp.homes.users.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private static final int TOKEN_BYTES = 64;
    private static final String REFRESH_COOKIE_NAME = "refreshToken";
    private static final String REFRESH_COOKIE_PATH = "/auth";
    private static final String REFRESH_COOKIE_SAME_SITE = "Lax";
    private static final boolean REFRESH_COOKIE_SECURE = true;

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public ResponseCookie issueCookie(User user, String deviceName, HttpServletRequest request) {
        String refreshToken = generateToken();
        String refreshTokenHash = hash(refreshToken);
        Instant now = Instant.now();

        refreshTokenRepository.save(buildRefreshToken(
                user,
                refreshTokenHash,
                deviceName,
                request.getHeader(HttpHeaders.USER_AGENT),
                request.getRemoteAddr(),
                now
        ));

        return createRefreshCookie(refreshToken);
    }

    @Transactional
    public RefreshTokenRotationDTO rotate(HttpServletRequest request) {
        String refreshToken = resolveRefreshToken(request);
        if (isBlank(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is required");
        }

        Instant now = Instant.now();
        RefreshToken savedToken = refreshTokenRepository.findByTokenHash(hash(refreshToken))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (savedToken.isExpired(now)) {
            refreshTokenRepository.delete(savedToken);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        User user = savedToken.getUser();
        String deviceName = savedToken.getDeviceName();
        refreshTokenRepository.delete(savedToken);

        ResponseCookie refreshCookie = issueCookie(user, deviceName, request);

        return new RefreshTokenRotationDTO(user, refreshCookie);
    }

    @Transactional
    public ResponseCookie logout(Long authenticatedUserId, HttpServletRequest request) {
        String refreshToken = resolveRefreshToken(request);
        if (isBlank(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is required");
        }

        RefreshToken savedToken = refreshTokenRepository.findByTokenHash(hash(refreshToken))
                .orElse(null);

        if (savedToken != null) {
            if (!savedToken.getUser().getId().equals(authenticatedUserId)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
            }

            refreshTokenRepository.delete(savedToken);
        }

        return deleteRefreshCookie();
    }

    private RefreshToken buildRefreshToken(
            User user,
            String refreshTokenHash,
            String deviceName,
            String userAgent,
            String ipAddress,
            Instant now
    ) {
        return RefreshToken.builder()
                .user(user)
                .tokenHash(refreshTokenHash)
                .expiresAt(now.plus(jwtProperties.refreshTokenExpiration()))
                .deviceName(defaultDeviceName(deviceName, userAgent))
                .userAgent(truncate(userAgent, 512))
                .ipAddress(ipAddress)
                .build();
    }

    private String resolveRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (REFRESH_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    private ResponseCookie createRefreshCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(REFRESH_COOKIE_SECURE)
                .sameSite(REFRESH_COOKIE_SAME_SITE)
                .path(REFRESH_COOKIE_PATH)
                .maxAge(jwtProperties.refreshTokenExpiration())
                .build();
    }

    private ResponseCookie deleteRefreshCookie() {
        return ResponseCookie.from(REFRESH_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(REFRESH_COOKIE_SECURE)
                .sameSite(REFRESH_COOKIE_SAME_SITE)
                .path(REFRESH_COOKIE_PATH)
                .maxAge(0)
                .build();
    }

    private String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is not available", e);
        }
    }

    private String defaultDeviceName(String deviceName, String userAgent) {
        if (!isBlank(deviceName)) {
            return deviceName;
        }
        if (!isBlank(userAgent)) {
            return truncate(userAgent, 120);
        }
        return "Unknown device";
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
