package com.chcorp.homes.users.controller;

import com.chcorp.homes.users.dto.request.LoginDTO;
import com.chcorp.homes.users.dto.request.RegisterDTO;
import com.chcorp.homes.users.dto.response.MyProfileDTO;
import com.chcorp.homes.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // 임시
    @GetMapping("/mypage")
    public ResponseEntity<?> mypage(@RequestParam String nickname) {
        log.info("Mypage nickname: {}", nickname);
        MyProfileDTO dto = userService.mypage(nickname);
        return ResponseEntity.ok(dto);
    }
}
