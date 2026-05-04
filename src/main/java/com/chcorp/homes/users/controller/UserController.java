package com.chcorp.homes.users.controller;

import com.chcorp.homes.users.dto.request.LoginDTO;
import com.chcorp.homes.users.dto.request.RegisterDTO;
import com.chcorp.homes.users.dto.response.MyProfileDTO;
import com.chcorp.homes.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        log.info("Login DTO: {}", loginDTO);
        String nickname = userService.login(loginDTO);
        return ResponseEntity.ok(nickname);

    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO registerDTO) {
        log.info("Register DTO: {}", registerDTO);
        String nickname = userService.register(registerDTO);
        return ResponseEntity.ok(nickname);
    }

    // 임시
    @GetMapping("/mypage")
    public ResponseEntity<?> mypage(@RequestParam String nickname) {
        log.info("Mypage nickname: {}", nickname);
        MyProfileDTO dto = userService.mypage(nickname);
        return ResponseEntity.ok(dto);
    }
}
