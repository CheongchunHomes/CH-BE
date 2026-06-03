package com.chcorp.homes.properties.service;

import com.chcorp.homes.files.service.FileService;
import com.chcorp.homes.properties.dto.PropertyDTO;
import com.chcorp.homes.properties.dto.PropertyThumbnailDTO;
import com.chcorp.homes.properties.entity.Property;
import com.chcorp.homes.properties.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final FileService fileService;

    public List<PropertyDTO> getMapProperties() {
        return propertyRepository.findByLatitudeIsNotNullAndLongitudeIsNotNull()
                .stream()
                .map(property -> PropertyDTO.from(property, resolvePublicThumbnailUrl(property.getThumbnailUrl())))
                .toList();
    }

    public PropertyDTO getProperty(Long propertyId) {
        Property property = findProperty(propertyId);
        return PropertyDTO.from(property, resolvePublicThumbnailUrl(property.getThumbnailUrl()));
    }

    public PropertyThumbnailDTO getPropertyThumbnail(Long propertyId) {
        Property property = findProperty(propertyId);
        return resolvePublicThumbnail(property.getThumbnailUrl());
    }

    private Property findProperty(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new NoSuchElementException("매물을 찾을 수 없습니다."));
    }

    private String resolvePublicThumbnailUrl(String thumbnailUrl) {
        return resolvePublicThumbnail(thumbnailUrl).thumbnailUrl();
    }

    private PropertyThumbnailDTO resolvePublicThumbnail(String thumbnailUrl) {
        Long fileId = fileService.parseFileReference(thumbnailUrl);
        if (fileId == null) {
            return new PropertyThumbnailDTO(thumbnailUrl, null);
        }

        var signedUrl = fileService.createPublicSignedDownloadUrl(fileId);
        return new PropertyThumbnailDTO(signedUrl.signedUrl(), signedUrl.expiresInSeconds());
    }
}
