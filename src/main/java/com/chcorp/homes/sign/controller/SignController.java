package com.chcorp.homes.sign.controller;

import com.chcorp.homes.files.dto.response.FileSignedUrlResponseDTO;
import com.chcorp.homes.sign.dto.request.CustomerSignRequestDTO;
import com.chcorp.homes.sign.dto.request.SignCreateRequestDTO;
import com.chcorp.homes.sign.dto.request.ProviderSignRequestDTO;
import com.chcorp.homes.sign.dto.response.BrokerSignImageResponseDTO;
import com.chcorp.homes.sign.dto.response.SignContractResponseDTO;
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
     * 계약서 상세
     * */
    @GetMapping("/{signId}/contract")
    public ResponseEntity<SignContractResponseDTO> contractDetail(
            Authentication authentication,
            @PathVariable Long signId
    ) {
        Long currentUserId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(signService.contractDetail(currentUserId, signId));
    }

    /**
     * 공인중개사 서명 이미지
     * */
    @GetMapping("/broker-sign")
    public ResponseEntity<BrokerSignImageResponseDTO> brokerSignImage(Authentication authentication) {
        Long currentUserId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(signService.brokerSignImage(currentUserId));
    }

    /**
     * provider 서명 PDF signed-url
     * */
    @GetMapping("/{signId}/provider-signed-pdf/signed-url")
    public ResponseEntity<FileSignedUrlResponseDTO> providerSignedPdfSignedUrl(
            Authentication authentication,
            @PathVariable Long signId
    ) {
        Long currentUserId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(signService.providerSignedPdfSignedUrl(currentUserId, signId));
    }

    /**
     * 완료 계약 PDF signed-url
     * */
    @GetMapping("/{signId}/completed-pdf/signed-url")
    public ResponseEntity<FileSignedUrlResponseDTO> completedPdfSignedUrl(
            Authentication authentication,
            @PathVariable Long signId
    ) {
        Long currentUserId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(signService.completedPdfSignedUrl(currentUserId, signId));
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
     * provider 서명
     * */
    @PostMapping("/{signId}/provider-sign")
    public ResponseEntity<SignResponseDTO> providerSign(
            Authentication authentication,
            @PathVariable Long signId,
            @RequestBody ProviderSignRequestDTO request
    ) {
        Long currentUserId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(signService.providerSign(currentUserId, signId, request));
    }

    /**
     * customer 서명
     * */
    @PostMapping("/{signId}/customer-sign")
    public ResponseEntity<SignResponseDTO> customerSign(
            Authentication authentication,
            @PathVariable Long signId,
            @RequestBody CustomerSignRequestDTO request
    ) {
        Long currentUserId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(signService.customerSign(currentUserId, signId, request));
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
