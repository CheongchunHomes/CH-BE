package com.chcorp.homes.auth.controller;

import com.chcorp.homes.auth.dto.request.AuthLoginDTO;
import com.chcorp.homes.auth.dto.request.AuthLogoutRequestDTO;
import com.chcorp.homes.auth.dto.request.AuthReauthRequestDTO;
import com.chcorp.homes.auth.dto.request.AuthRefreshRequestDTO;
import com.chcorp.homes.auth.dto.response.AccessTokenResponseDTO;
import com.chcorp.homes.auth.dto.response.AuthErrorResponseDTO;
import com.chcorp.homes.auth.dto.response.AuthLoginResponseDTO;
import com.chcorp.homes.auth.dto.response.AuthUserResponse;
import com.chcorp.homes.auth.exception.ReauthRequiredException;
import com.chcorp.homes.auth.exception.RefreshExpiredException;
import com.chcorp.homes.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponseDTO> login(
            @RequestBody AuthLoginDTO request,
            HttpServletRequest servletRequest
    ) {
        return ResponseEntity.ok(authService.login(request, servletRequest));
    }

    /**
     * 엑세스 토큰 재발급
     * BFF가 refresh token을 body로 전달한다.
     * 70% 초과 → 401 REAUTH_REQUIRED
     * 만료 → 401 REFRESH_EXPIRED
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody AuthRefreshRequestDTO request) {
        try {
            AccessTokenResponseDTO response = authService.refresh(request.refreshToken());
            return ResponseEntity.ok(response);
        } catch (ReauthRequiredException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthErrorResponseDTO("REAUTH_REQUIRED"));
        } catch (RefreshExpiredException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthErrorResponseDTO("REFRESH_EXPIRED"));
        }
    }

    /**
     * BFF가 refresh token과 password를 body로 전달한다.
     * 성공 시 access token을 재발급하고 refresh token 세션을 갱신한다.
     * refreshExpiresAt은 BFF가 refresh cookie maxAge 갱신에 사용한다.
     */
    @PostMapping("/reauth")
    public ResponseEntity<?> reauth(@RequestBody AuthReauthRequestDTO request) {
        try {
            return ResponseEntity.ok(authService.reauth(request));
        } catch (RefreshExpiredException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthErrorResponseDTO("REFRESH_EXPIRED"));
        } catch (ResponseStatusException e) {
            if (e.getStatusCode().value() == HttpStatus.UNAUTHORIZED.value() && "Invalid credentials".equals(e.getReason())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthErrorResponseDTO("INVALID_CREDENTIALS"));
            }
            if (e.getStatusCode().value() == HttpStatus.UNAUTHORIZED.value()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthErrorResponseDTO("UNAUTHENTICATED"));
            }
            throw e;
        }
    }

    /**
     * BFF가 refresh token을 body로 전달한다.
     * DB row 삭제만 수행한다. cookie 삭제는 BFF가 담당한다.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody AuthLogoutRequestDTO request
    ) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    /**
     * BFF가 access token을 Authorization header로 변환해 전달한다.
     * 로그인 상태 초기화와 Navbar user 정보 표시 기준으로 사용된다.
     */
    @GetMapping("/me")
    public ResponseEntity<AuthUserResponse> me(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        String authLevel = authentication.getDetails() instanceof String details ? details : null;
        return ResponseEntity.ok(authService.me(userId, authLevel));
    }
}
