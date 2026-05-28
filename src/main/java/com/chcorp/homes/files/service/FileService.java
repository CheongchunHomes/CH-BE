package com.chcorp.homes.files.service;

import com.chcorp.homes.files.dto.request.FileUploadUrlRequestDTO;
import com.chcorp.homes.files.dto.response.FileSignedUrlResponseDTO;
import com.chcorp.homes.files.dto.response.FileUploadUrlResponseDTO;
import com.chcorp.homes.files.entity.FileAsset;
import com.chcorp.homes.files.entity.FileContentType;
import com.chcorp.homes.files.entity.FileStatus;
import com.chcorp.homes.files.repository.FileAssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private static final long SIGNED_UPLOAD_EXPIRES_IN_SECONDS = 7200L;
    private static final String FILE_REFERENCE_PREFIX = "file:";
    private static final String PROPERTY_IMAGE_PATH_PREFIX = "properties/images/";
    private static final Long PUBLIC_PROPERTY_IMAGE_OWNER_ID = 0L;

    private final FileAssetRepository fileAssetRepository;
    private final FileObjectPathGenerator objectPathGenerator;
    private final FileAccessPolicy fileAccessPolicy;
    private final SupabaseStorageClient supabaseStorageClient;

    @Transactional
    public FileUploadUrlResponseDTO createUploadUrl(Long userId, FileUploadUrlRequestDTO request) {
        String originalFilename = normalizeOriginalFilename(request.originalFilename());
        FileContentType contentType = request.contentType() == null
                ? FileContentType.UNKNOWN
                : request.contentType();
        String objectPath = objectPathGenerator.generate(userId, originalFilename);

        FileAsset fileAsset = FileAsset.builder()
                .ownerUserId(userId)
                .objectPath(objectPath)
                .originalFilename(originalFilename)
                .contentType(contentType)
                .sizeBytes(request.sizeBytes())
                .status(FileStatus.PENDING)
                .build();

        return savePendingFileAndCreateUploadUrl(fileAsset);
    }

    @Transactional
    public void completeUpload(Long userId, Long fileId) {
        FileAsset fileAsset = findFileAsset(fileId);
        if (!fileAccessPolicy.canCompleteUpload(fileAsset, userId)) {
            throw new AccessDeniedException("파일 업로드를 완료할 권한이 없습니다.");
        }

        completePendingUpload(fileAsset);
    }

    @Transactional
    public FileUploadUrlResponseDTO createPublicPropertyImageUploadUrl(FileUploadUrlRequestDTO request) {
        String originalFilename = normalizeOriginalFilename(request.originalFilename());
        String objectPath = objectPathGenerator.generatePropertyImage(request.propertyId(), originalFilename);

        FileAsset fileAsset = FileAsset.builder()
                .ownerUserId(PUBLIC_PROPERTY_IMAGE_OWNER_ID)
                .objectPath(objectPath)
                .originalFilename(originalFilename)
                .contentType(FileContentType.IMAGE)
                .sizeBytes(request.sizeBytes())
                .status(FileStatus.PENDING)
                .build();

        return savePendingFileAndCreateUploadUrl(fileAsset);
    }

    @Transactional
    public void completePublicPropertyImageUpload(Long fileId) {
        FileAsset fileAsset = findFileAsset(fileId);
        validatePropertyImageAsset(fileAsset);
        completePendingUpload(fileAsset);
    }

    public FileSignedUrlResponseDTO createSignedDownloadUrl(Long userId, Long fileId) {
        FileAsset fileAsset = findFileAsset(fileId);
        if (fileAsset.getStatus() != FileStatus.ACTIVE) {
            throw new IllegalStateException("다운로드 URL을 발급할 수 없는 파일 상태입니다.");
        }
        if (!fileAccessPolicy.canRead(fileAsset, userId)) {
            throw new AccessDeniedException("파일을 조회할 권한이 없습니다.");
        }

        return createSignedUrlResponse(fileAsset);
    }

    public FileSignedUrlResponseDTO createPublicSignedDownloadUrl(Long fileId) {
        FileAsset fileAsset = findFileAsset(fileId);
        validatePropertyImageAsset(fileAsset);
        if (fileAsset.getStatus() != FileStatus.ACTIVE) {
            throw new IllegalStateException("다운로드 URL을 발급할 수 없는 파일 상태입니다.");
        }

        return createSignedUrlResponse(fileAsset);
    }

    public Long parseFileReference(String fileReference) {
        if (fileReference == null || !fileReference.startsWith(FILE_REFERENCE_PREFIX)) {
            return null;
        }

        String fileId = fileReference.substring(FILE_REFERENCE_PREFIX.length());
        if (fileId.isBlank()) {
            return null;
        }

        return Long.valueOf(fileId);
    }

    @Transactional
    public void deleteFile(Long userId, Long fileId) {
        FileAsset fileAsset = findFileAsset(fileId);
        if (!fileAccessPolicy.canDelete(fileAsset, userId)) {
            throw new AccessDeniedException("파일을 삭제할 권한이 없습니다.");
        }
        if (fileAsset.getStatus() == FileStatus.DELETED) {
            return;
        }

        supabaseStorageClient.delete(fileAsset.getObjectPath());
        fileAsset.delete();
    }

    private FileUploadUrlResponseDTO savePendingFileAndCreateUploadUrl(FileAsset fileAsset) {
        FileAsset savedFileAsset = fileAssetRepository.save(fileAsset);
        String signedUploadUrl = supabaseStorageClient.createSignedUploadUrl(savedFileAsset.getObjectPath());

        return new FileUploadUrlResponseDTO(
                savedFileAsset.getId(),
                savedFileAsset.getObjectPath(),
                signedUploadUrl,
                SIGNED_UPLOAD_EXPIRES_IN_SECONDS
        );
    }

    private void completePendingUpload(FileAsset fileAsset) {
        if (fileAsset.getStatus() != FileStatus.PENDING) {
            throw new IllegalStateException("업로드 완료 처리할 수 없는 파일 상태입니다.");
        }
        if (!supabaseStorageClient.exists(fileAsset.getObjectPath())) {
            throw new IllegalStateException("Supabase Storage에 업로드된 파일이 없습니다.");
        }

        fileAsset.completeUpload();
    }

    private FileSignedUrlResponseDTO createSignedUrlResponse(FileAsset fileAsset) {
        String signedUrl = supabaseStorageClient.createSignedDownloadUrl(fileAsset.getObjectPath());

        return new FileSignedUrlResponseDTO(
                fileAsset.getId(),
                signedUrl,
                supabaseStorageClient.signedDownloadTtlSeconds(),
                fileAsset.getContentType(),
                fileAsset.getOriginalFilename(),
                fileAsset.getSizeBytes()
        );
    }

    private void validatePropertyImageAsset(FileAsset fileAsset) {
        if (!fileAsset.getObjectPath().startsWith(PROPERTY_IMAGE_PATH_PREFIX)) {
            throw new AccessDeniedException("물건 이미지 파일만 공개 처리할 수 있습니다.");
        }
        if (fileAsset.getContentType() != FileContentType.IMAGE) {
            throw new AccessDeniedException("이미지 파일만 공개 처리할 수 있습니다.");
        }
    }

    private FileAsset findFileAsset(Long fileId) {
        return fileAssetRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));
    }

    private String normalizeOriginalFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "file";
        }

        return originalFilename.trim();
    }
}
