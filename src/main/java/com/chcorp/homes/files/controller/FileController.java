package com.chcorp.homes.files.controller;

import com.chcorp.homes.files.dto.request.FileUploadUrlRequestDTO;
import com.chcorp.homes.files.dto.response.FileSignedUrlResponseDTO;
import com.chcorp.homes.files.dto.response.FileUploadUrlResponseDTO;
import com.chcorp.homes.files.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    // 로그인 사용자의 일반 파일 업로드 URL을 발급합니다.
    @PostMapping("/upload-url")
    public ResponseEntity<FileUploadUrlResponseDTO> createUploadUrl(
            Authentication authentication,
            @RequestBody FileUploadUrlRequestDTO request
    ) {
        Long userId = Long.valueOf(authentication.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(fileService.createUploadUrl(userId, request));
    }

    // 로그인 사용자의 일반 파일 업로드 완료를 확정합니다.
    @PostMapping("/{fileId}/complete")
    public ResponseEntity<Void> completeUpload(
            Authentication authentication,
            @PathVariable Long fileId
    ) {
        Long userId = Long.valueOf(authentication.getName());

        fileService.completeUpload(userId, fileId);

        return ResponseEntity.noContent().build();
    }

    // 관리자 물건 이미지 전용 public 업로드 URL입니다. 브라우저가 이 URL로 Supabase에 직접 업로드합니다.
    @PostMapping("/property-images/upload-url")
    public ResponseEntity<FileUploadUrlResponseDTO> createPropertyImageUploadUrl(
            @RequestBody FileUploadUrlRequestDTO request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(fileService.createPublicPropertyImageUploadUrl(request));
    }

    // 관리자 물건 이미지 전용 public 업로드 완료 API입니다. Supabase 업로드 여부를 확인한 뒤 ACTIVE로 바꿉니다.
    @PostMapping("/property-images/{fileId}/complete")
    public ResponseEntity<Void> completePropertyImageUpload(@PathVariable Long fileId) {
        fileService.completePublicPropertyImageUpload(fileId);

        return ResponseEntity.noContent().build();
    }

    // 지도와 상세 화면에서 물건 이미지를 보여주기 위한 public signed download URL을 발급합니다.
    @GetMapping("/property-images/{fileId}/signed-url")
    public ResponseEntity<FileSignedUrlResponseDTO> createPropertyImageSignedUrl(@PathVariable Long fileId) {
        return ResponseEntity.ok(fileService.createPublicSignedDownloadUrl(fileId));
    }

    // 로그인 사용자의 일반 파일 다운로드 URL을 발급합니다.
    @GetMapping("/{fileId}/signed-url")
    public ResponseEntity<FileSignedUrlResponseDTO> createSignedDownloadUrl(
            Authentication authentication,
            @PathVariable Long fileId
    ) {
        Long userId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(
                fileService.createSignedDownloadUrl(userId, fileId)
        );
    }

    // 로그인 사용자의 일반 파일을 삭제합니다.
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(
            Authentication authentication,
            @PathVariable Long fileId
    ) {
        Long userId = Long.valueOf(authentication.getName());

        fileService.deleteFile(userId, fileId);

        return ResponseEntity.noContent().build();
    }
}
