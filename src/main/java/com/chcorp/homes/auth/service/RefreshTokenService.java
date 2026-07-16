package com.chcorp.homes.auth.service;

import com.chcorp.homes.auth.entity.RefreshToken;
import com.chcorp.homes.auth.exception.RefreshExpiredException;
import com.chcorp.homes.auth.exception.ReauthRequiredException;
import com.chcorp.homes.auth.repository.RefreshTokenRepository;
import com.chcorp.homes.common.config.JwtProperties;
import com.chcorp.homes.users.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    /**
     * Refresh token 수명의 70%가 지나면 재인증이 필요하다.
     * threshold = issuedAt + (expiresAt - issuedAt) * 0.7
     */
    private static final double REAUTH_THRESHOLD = 0.7;
    private static final String UNKNOWN_USER_AGENT = "UNKNOWN";

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    private final RefreshTokenGenerator refreshTokenGenerator;

    /**
     * 로그인 성공 후 refresh token을 발급하고 raw token과 만료 시각을 반환한다.
     * BFF가 이 값을 HttpOnly cookie로 저장한다.
     */
    @Transactional
    public IssuedRefreshToken issue(User user, HttpServletRequest request) {
        String rawToken = refreshTokenGenerator.generateToken();
        String tokenHash = refreshTokenGenerator.hash(rawToken);
        Instant now = Instant.now();
        Instant expiresAt = now.plus(jwtProperties.refreshTokenExpiration());

        refreshTokenRepository.save(buildRefreshToken(user, tokenHash, request, expiresAt));

        return new IssuedRefreshToken(rawToken, expiresAt);
    }

    public record IssuedRefreshToken(String token, Instant expiresAt) {
    }

    @Transactional
    public void replaceLoginSession(User user, HttpServletRequest request) {
        refreshTokenRepository.deleteSessionsByUserAndUserAgent(
                user,
                resolveUserAgent(request)
        );
    }

    /**
     * /auth/refresh 처리.
     * - 만료: RefreshExpiredException → 401 REFRESH_EXPIRED
     * - 70% 초과: ReauthRequiredException → 401 REAUTH_REQUIRED
     * - 정상: 토큰 소유 User 반환 (rotation 없음)
     */
    @Transactional
    public User validateForRefresh(String rawToken) {
        RefreshToken savedToken = findByRawToken(rawToken);
        Instant now = Instant.now();

        deleteAndRejectIfExpired(savedToken, now);

        if (isPastReauthThreshold(savedToken, now)) {
            throw new ReauthRequiredException();
        }

        return savedToken.getUser();
    }

    /**
     * /auth/reauth 처리.
     * 재인증 성공 시 기존 row의 reauthedAt과 expiresAt을 갱신한다. row는 삭제하지 않는다.
     * 갱신된 expiresAt을 반환한다 (BFF가 cookie maxAge 갱신에 사용).
     */
    @Transactional
    public Instant reauth(User user, String rawToken) {
        RefreshToken savedToken = findByRawToken(rawToken);

        if (!savedToken.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        Instant now = Instant.now();
        Instant newExpiresAt = now.plus(jwtProperties.refreshTokenExpiration());
        savedToken.updateForReauth(now, newExpiresAt);

        return newExpiresAt;
    }

    /**
     * /auth/logout 처리. 현재 기기 세션만 삭제한다.
     */
    @Transactional
    public void logout(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is required");
        }

        RefreshToken savedToken = refreshTokenRepository.findByTokenHash(refreshTokenGenerator.hash(rawToken))
                .orElse(null);

        if (savedToken != null) {
            refreshTokenRepository.delete(savedToken);
        }
    }

    /**
     * /auth/reauth 에서 세션 소유 user를 식별한다.
     * 만료된 token은 재인증으로 되살릴 수 없다. 요구사항: "만료 시 다시 로그인".
     * 70% 임계치 초과 여부는 검사하지 않는다 (재인증 목적이 갱신이므로).
     */
    @Transactional
    public User findUserByRefreshToken(String rawToken) {
        RefreshToken savedToken = findByRawToken(rawToken);
        deleteAndRejectIfExpired(savedToken, Instant.now());
        return savedToken.getUser();
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private RefreshToken findByRawToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is required");
        }
        return refreshTokenRepository.findByTokenHash(refreshTokenGenerator.hash(rawToken))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));
    }

    private void deleteAndRejectIfExpired(RefreshToken token, Instant now) {
        if (token.isExpired(now)) {
            refreshTokenRepository.delete(token);
            throw new RefreshExpiredException();
        }
    }

    /**
     * 70% 임계치 초과 여부를 판단한다.
     * 재인증 성공 후에는 reauthedAt이 새 세션 시작점이 되므로 reauthedAt을 기준으로 한다.
     * reauthedAt이 없으면 최초 발급 시각인 createdAt을 기준으로 한다.
     * threshold = startAt + (expiresAt - startAt) * 0.7
     */
    private boolean isPastReauthThreshold(RefreshToken token, Instant now) {
        Instant startAt = token.getReauthedAt() != null ? token.getReauthedAt() : token.getCreatedAt();
        long totalSeconds = token.getExpiresAt().getEpochSecond() - startAt.getEpochSecond();
        long thresholdSeconds = (long) (totalSeconds * REAUTH_THRESHOLD);
        Instant threshold = startAt.plusSeconds(thresholdSeconds);
        return now.isAfter(threshold);
    }

    private RefreshToken buildRefreshToken(User user, String tokenHash, HttpServletRequest request, Instant expiresAt) {
        return RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(expiresAt)
                .userAgent(resolveUserAgent(request))
                .build();
    }

    private String resolveUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        if (userAgent == null || userAgent.isBlank()) {
            return UNKNOWN_USER_AGENT;
        }
        return truncate(userAgent, 512);
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
