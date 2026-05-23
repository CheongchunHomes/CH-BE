package com.chcorp.homes.files.service;

import com.chcorp.homes.files.entity.FileAsset;
import org.springframework.stereotype.Component;

@Component
public class FileAccessPolicy {

    public boolean canRead(FileAsset fileAsset, Long userId) {
        return isOwner(fileAsset, userId);
    }

    public boolean canDelete(FileAsset fileAsset, Long userId) {
        return isOwner(fileAsset, userId);
    }

    public boolean canCompleteUpload(FileAsset fileAsset, Long userId) {
        return isOwner(fileAsset, userId);
    }

    private boolean isOwner(FileAsset fileAsset, Long userId) {
        return fileAsset.getOwnerUserId().equals(userId);
    }
}
