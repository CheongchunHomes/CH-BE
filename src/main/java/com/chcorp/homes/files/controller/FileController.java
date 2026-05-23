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

    @PostMapping("/{fileId}/complete")
    public ResponseEntity<Void> completeUpload(
            Authentication authentication,
            @PathVariable Long fileId
    ) {
        Long userId = Long.valueOf(authentication.getName());

        fileService.completeUpload(userId, fileId);

        return ResponseEntity.noContent().build();
    }

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
