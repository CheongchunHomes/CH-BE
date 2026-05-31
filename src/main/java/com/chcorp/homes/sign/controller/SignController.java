package com.chcorp.homes.sign.controller;

import com.chcorp.homes.files.dto.response.FileSignedUrlResponseDTO;
import com.chcorp.homes.sign.dto.request.CustomerSignRequestDTO;
import com.chcorp.homes.sign.dto.request.ProviderSignRequestDTO;
import com.chcorp.homes.sign.dto.request.SignCreateRequestDTO;
import com.chcorp.homes.sign.dto.response.BrokerSignImageResponseDTO;
import com.chcorp.homes.sign.dto.response.SignContractResponseDTO;
import com.chcorp.homes.sign.dto.response.SignResponseDTO;
import com.chcorp.homes.sign.service.SignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/sign")
public class SignController {

    private final SignService signService;

    @GetMapping("/my")
    public ResponseEntity<List<SignResponseDTO>> myList(Authentication authentication) {
        Long currentUserId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(signService.myList(currentUserId));
    }

    @GetMapping("/{signId}/contract")
    public ResponseEntity<SignContractResponseDTO> contractDetail(
            Authentication authentication,
            @PathVariable Long signId
    ) {
        Long currentUserId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(signService.contractDetail(currentUserId, signId));
    }

    @GetMapping("/broker-sign")
    public ResponseEntity<BrokerSignImageResponseDTO> brokerSignImage(Authentication authentication) {
        Long currentUserId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(signService.brokerSignImage(currentUserId));
    }

    @GetMapping("/{signId}/completed-pdf/signed-url")
    public ResponseEntity<FileSignedUrlResponseDTO> completedPdfSignedUrl(
            Authentication authentication,
            @PathVariable Long signId
    ) {
        Long currentUserId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(signService.completedPdfSignedUrl(currentUserId, signId));
    }

    @GetMapping("/{signId}/files/{fileId}/signed-url")
    public ResponseEntity<FileSignedUrlResponseDTO> contractFileSignedUrl(
            Authentication authentication,
            @PathVariable Long signId,
            @PathVariable Long fileId
    ) {
        Long currentUserId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(signService.contractFileSignedUrl(currentUserId, signId, fileId));
    }

    @PostMapping
    public ResponseEntity<SignResponseDTO> issue(
            Authentication authentication,
            @RequestBody SignCreateRequestDTO request
    ) {
        Long currentUserId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(signService.issue(currentUserId, request));
    }

    @PostMapping("/{signId}/provider-sign")
    public ResponseEntity<SignResponseDTO> providerSign(
            Authentication authentication,
            @PathVariable Long signId,
            @RequestBody ProviderSignRequestDTO request
    ) {
        Long currentUserId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(signService.providerSign(currentUserId, signId, request));
    }

    @PostMapping("/{signId}/customer-sign")
    public ResponseEntity<SignResponseDTO> customerSign(
            Authentication authentication,
            @PathVariable Long signId,
            @RequestBody CustomerSignRequestDTO request
    ) {
        Long currentUserId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(signService.customerSign(currentUserId, signId, request));
    }

    @PostMapping("/{signId}/cancel")
    public ResponseEntity<SignResponseDTO> cancel(
            Authentication authentication,
            @PathVariable Long signId
    ) {
        Long currentUserId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(signService.cancel(currentUserId, signId));
    }
}
