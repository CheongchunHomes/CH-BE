package com.chcorp.homes.files.entity;

import com.chcorp.homes.common.entity.MutableBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "file_assets")
public class FileAsset extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_user_id", nullable = false)
    private Long ownerUserId;

    @Column(name = "object_path", nullable = false, unique = true, length = 500)
    private String objectPath;

    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, length = 30)
    private FileContentType contentType;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FileStatus status;

    public void completeUpload() {
        this.status = FileStatus.ACTIVE;
    }

    public void delete() {
        this.status = FileStatus.DELETED;
    }
}
