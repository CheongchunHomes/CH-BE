package com.chcorp.homes.sign.controller;

import com.chcorp.homes.sign.dto.request.SignCreateRequestDTO;
import com.chcorp.homes.sign.dto.response.SignResponseDTO;
import com.chcorp.homes.sign.service.SignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/sign")
public class SignController {

    private final SignService signService;

    /**
     * 내 계약서 리스트
     * */
    @GetMapping("/my")
    public ResponseEntity<List<SignResponseDTO>> myList(Authentication authentication) {
        Long currentUserId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(signService.myList(currentUserId));
    }


    /**
     * 계약서 생성
     * */
    @PostMapping
    public ResponseEntity<SignResponseDTO> issue(
            Authentication authentication,
            @RequestBody SignCreateRequestDTO request
    ) {
        Long currentUserId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(signService.issue(currentUserId, request));
    }



    /**
     * 계약서 승인
     * */
    @PostMapping("/{signId}/approve")
    public ResponseEntity<SignResponseDTO> approve(
            Authentication authentication,
            @PathVariable Long signId
    ) {
        Long currentUserId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(signService.approve(currentUserId, signId));
    }


    /**
     * 계약서 취소
     * */
    @PostMapping("/{signId}/cancel")
    public ResponseEntity<SignResponseDTO> cancel(
            Authentication authentication,
            @PathVariable Long signId
    ) {
        Long currentUserId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(signService.cancel(currentUserId, signId));
    }


}
