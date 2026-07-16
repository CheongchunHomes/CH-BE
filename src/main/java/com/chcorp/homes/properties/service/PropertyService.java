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

    private static final int DEFAULT_PROPERTY_IMAGE_COUNT = 10;
    private static final String DEFAULT_PROPERTY_IMAGE_PATH = "/images/properties/default-%d.jpg";

    private final PropertyRepository propertyRepository;
    private final FileService fileService;

    public List<PropertyDTO> getMapProperties() {
        return propertyRepository.findByLatitudeIsNotNullAndLongitudeIsNotNull()
                .stream()
                .map(property -> PropertyDTO.from(property, resolvePublicThumbnailUrl(property)))
                .toList();
    }

    public PropertyDTO getProperty(Long propertyId) {
        Property property = findProperty(propertyId);
        return PropertyDTO.from(property, resolvePublicThumbnailUrl(property));
    }

    public PropertyThumbnailDTO getPropertyThumbnail(Long propertyId) {
        Property property = findProperty(propertyId);
        return resolvePublicThumbnail(property);
    }

    private Property findProperty(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new NoSuchElementException("매물을 찾을 수 없습니다."));
    }

    private String resolvePublicThumbnailUrl(Property property) {
        return resolvePublicThumbnail(property).thumbnailUrl();
    }

    private PropertyThumbnailDTO resolvePublicThumbnail(Property property) {
        String thumbnailUrl = property.getThumbnailUrl();
        Long fileId = fileService.parseFileReference(thumbnailUrl);
        if (fileId == null) {
            String resolvedThumbnailUrl = thumbnailUrl == null || thumbnailUrl.isBlank()
                    ? resolveFallbackThumbnailUrl(property.getId())
                    : thumbnailUrl;
            return new PropertyThumbnailDTO(resolvedThumbnailUrl, null);
        }

        try {
            var signedUrl = fileService.createPublicSignedDownloadUrl(fileId);
            return new PropertyThumbnailDTO(signedUrl.signedUrl(), signedUrl.expiresInSeconds());
        } catch (RuntimeException e) {
            return new PropertyThumbnailDTO(resolveFallbackThumbnailUrl(property.getId()), null);
        }
    }

    private String resolveFallbackThumbnailUrl(Long propertyId) {
        long seed = propertyId == null ? 0L : propertyId - 1L;
        int imageNumber = Math.floorMod(seed, DEFAULT_PROPERTY_IMAGE_COUNT) + 1;
        return DEFAULT_PROPERTY_IMAGE_PATH.formatted(imageNumber);
    }
}
