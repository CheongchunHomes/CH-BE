package com.chcorp.homes.auth.service;

import com.chcorp.homes.auth.dto.request.AuthLoginDTO;
import com.chcorp.homes.auth.dto.request.AuthReauthRequestDTO;
import com.chcorp.homes.auth.dto.response.AccessTokenResponseDTO;
import com.chcorp.homes.auth.dto.response.AuthLoginResponseDTO;
import com.chcorp.homes.auth.dto.response.AuthUserResponse;
import com.chcorp.homes.auth.dto.response.ReauthResponseDTO;
import com.chcorp.homes.common.config.JwtTokenProvider;
import com.chcorp.homes.users.entity.User;
import com.chcorp.homes.users.repository.UserRepository;
import com.chcorp.homes.users.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    /**
     * 로그인 성공 시 access token과 refresh token을 body로 반환한다.
     * cookie 발급은 BFF가 담당한다.
     */
    @Transactional
    public Optional<AuthLoginResponseDTO> login(AuthLoginDTO request, HttpServletRequest servletRequest) {
        User user = authenticate(request);

        if (refreshTokenService.hasActiveSession(user, servletRequest)) {
            return Optional.empty();
        }

        String refreshToken = refreshTokenService.issue(user, servletRequest);

        return Optional.of(new AuthLoginResponseDTO(createAccessToken(user), refreshToken));
    }

    /**
     * /auth/refresh.
     * 70% 임계치 및 만료 판단은 RefreshTokenService에서 예외로 처리한다.
     * 정상이면 access token만 재발급한다 (rotation 없음).
     */
    @Transactional(readOnly = true)
    public AccessTokenResponseDTO refresh(String rawRefreshToken) {
        User user = refreshTokenService.validateForRefresh(rawRefreshToken);
        return new AccessTokenResponseDTO(createAccessToken(user));
    }

    /**
     * /auth/reauth.
     * refresh token으로 세션 사용자를 식별하고 password를 검증한다.
     * 성공 시 access token을 재발급하고 refresh token 세션의 reauthedAt/expiresAt을 갱신한다.
     * refreshExpiresAt은 BFF가 refresh cookie maxAge 갱신에 사용한다.
     */
    @Transactional
    public ReauthResponseDTO reauth(AuthReauthRequestDTO request) {
        if (request == null || isBlank(request.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }
        if (isBlank(request.refreshToken())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is required");
        }

        User sessionUser = refreshTokenService.findUserByRefreshToken(request.refreshToken());

        User user = userService.findById(sessionUser.getId());

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        Instant newRefreshExpiresAt = refreshTokenService.reauth(user, request.refreshToken());

        return new ReauthResponseDTO(createAccessToken(user), newRefreshExpiresAt);
    }

    /**
     * /auth/me.
     * 인증된 사용자 정보를 반환한다.
     * BFF 로그인 상태 초기화와 Navbar 표시 기준으로 사용된다.
     */
    @Transactional(readOnly = true)
    public AuthUserResponse me(Long userId) {
        User user = userService.findById(userId);
        return AuthUserResponse.from(user);
    }

    /**
     * /auth/logout. refresh token row 삭제만 수행한다.
     * cookie 삭제는 BFF가 담당한다.
     */
    @Transactional
    public void logout(String rawRefreshToken) {
        refreshTokenService.logout(rawRefreshToken);
    }

    private User authenticate(AuthLoginDTO request) {
        validateLoginRequest(request);

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        return user;
    }

    private String createAccessToken(User user) {
        return jwtTokenProvider.createAccessToken(user.getId(), user.getRole().name());
    }

    private void validateLoginRequest(AuthLoginDTO request) {
        if (request == null || isBlank(request.email()) || isBlank(request.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email and password are required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
