package com.chcorp.homes.files.repository;

import com.chcorp.homes.files.entity.FileAsset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileAssetRepository extends JpaRepository<FileAsset, Long> {
}
