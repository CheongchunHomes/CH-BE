package com.chcorp.homes.files.repository;

import com.chcorp.homes.files.entity.FileAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileAssetRepository extends JpaRepository<FileAsset, Long> {

    Optional<FileAsset> findByObjectPath(String objectPath);
}
