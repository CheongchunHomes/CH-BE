package com.chcorp.homes.users.controller;

import com.chcorp.homes.auth.dto.response.AuthErrorResponseDTO;
import com.chcorp.homes.users.dto.request.PersonalInfoRequestDTO;
import com.chcorp.homes.users.dto.request.PasswordChangeRequestDTO;
import com.chcorp.homes.users.dto.request.RegisterDTO;
import com.chcorp.homes.users.dto.response.MyProfileDTO;
import com.chcorp.homes.users.dto.response.NicknameCheckResponseDTO;
import com.chcorp.homes.users.dto.response.PersonalInfoResponseDTO;
import com.chcorp.homes.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/nickname/check")
    public ResponseEntity<NicknameCheckResponseDTO> checkNickname(@RequestParam String nickname) {
        boolean available = userService.checkNicknameAvailable(nickname);
        return ResponseEntity.ok(new NicknameCheckResponseDTO(available));
    }

    @GetMapping("/mypage")
    public ResponseEntity<MyProfileDTO> mypage(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        log.info("Mypage userId: {}", userId);
        MyProfileDTO dto = userService.mypage(userId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/personal")
    public ResponseEntity<Void> personal(
            Authentication authentication,
            @RequestBody PersonalInfoRequestDTO request
    ) {
        Long userId = Long.valueOf(authentication.getName());
        userService.createPersonalInfo(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @RequestBody PasswordChangeRequestDTO request
    ) {
        Long userId = Long.valueOf(authentication.getName());
        userService.changePassword(userId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/personal")
    public ResponseEntity<PersonalInfoResponseDTO> getPersonalInfo(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(userService.getPersonalInfo(userId));
    }

    @PutMapping("/personal")
    public ResponseEntity<Void> upsertPersonalInfo(
            Authentication authentication,
            @RequestBody PersonalInfoRequestDTO request
    ) {
        Long userId = Long.valueOf(authentication.getName());
        userService.upsertPersonalInfo(userId, request);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(SensitiveAuthRequiredException.class)
    public ResponseEntity<AuthErrorResponseDTO> handleSensitiveAuthRequired() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthErrorResponseDTO("REAUTH_REQUIRED"));
    }

    private void requireSensitiveAuthLevel(Authentication authentication) {
        String authLevel = authentication.getDetails() instanceof String details ? details : null;

        if (!"LOGIN".equals(authLevel) && !"REAUTH".equals(authLevel)) {
            throw new SensitiveAuthRequiredException();
        }
    }

    private static class SensitiveAuthRequiredException extends RuntimeException {
    }
}
