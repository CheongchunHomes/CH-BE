package com.chcorp.homes.auth.service;

import com.chcorp.homes.auth.dto.AuthResponseWithCookieDTO;
import com.chcorp.homes.auth.dto.RefreshTokenRotationDTO;
import com.chcorp.homes.auth.dto.request.AuthLoginDTO;
import com.chcorp.homes.auth.dto.response.AccessTokenResponseDTO;
import com.chcorp.homes.common.config.JwtTokenProvider;
import com.chcorp.homes.users.entity.User;
import com.chcorp.homes.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthResponseWithCookieDTO login(AuthLoginDTO request, HttpServletRequest servletRequest) {
        User user = authenticate(request);
        ResponseCookie refreshCookie = refreshTokenService.issueCookie(user, request.deviceName(), servletRequest);

        return new AuthResponseWithCookieDTO(createTokenResponse(user), refreshCookie);
    }

    @Transactional
    public AuthResponseWithCookieDTO refresh(HttpServletRequest servletRequest) {
        RefreshTokenRotationDTO rotation = refreshTokenService.rotate(servletRequest);

        return new AuthResponseWithCookieDTO(createTokenResponse(rotation.user()), rotation.refreshCookie());
    }

    @Transactional
    public ResponseCookie logout(Long authenticatedUserId, HttpServletRequest servletRequest) {
        return refreshTokenService.logout(authenticatedUserId, servletRequest);
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

    private AccessTokenResponseDTO createTokenResponse(User user) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole().name());

        return new AccessTokenResponseDTO(accessToken);
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
