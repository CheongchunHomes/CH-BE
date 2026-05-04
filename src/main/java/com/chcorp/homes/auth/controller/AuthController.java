package com.chcorp.homes.auth.controller;

import com.chcorp.homes.auth.dto.AuthResponseWithCookieDTO;
import com.chcorp.homes.auth.dto.request.AuthLoginDTO;
import com.chcorp.homes.auth.dto.response.AccessTokenResponseDTO;
import com.chcorp.homes.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponseDTO> login(
            @RequestBody AuthLoginDTO request,
            HttpServletRequest servletRequest
    ) {
        AuthResponseWithCookieDTO authResponse = authService.login(request, servletRequest);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResponse.cookie().toString())
                .body(authResponse.response());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponseDTO> refresh(HttpServletRequest servletRequest) {
        AuthResponseWithCookieDTO authResponse = authService.refresh(servletRequest);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResponse.cookie().toString())
                .body(authResponse.response());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication, HttpServletRequest servletRequest) {
        Long userId = Long.valueOf(authentication.getName());
        ResponseCookie deleteCookie = authService.logout(userId, servletRequest);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }
}
