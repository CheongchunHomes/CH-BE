package com.chcorp.homes.files.service;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

@Component
public class FileObjectPathGenerator {

    public String generate(Long userId, String originalFilename) {
        String extension = extractSafeExtension(originalFilename);
        return "users/" + userId + "/files/" + UUID.randomUUID() + extension;
    }

    public String generatePropertyImage(Long propertyId, String originalFilename) {
        String extension = extractSafeExtension(originalFilename);
        String propertyDirectory = propertyId == null ? "temp" : propertyId.toString();
        return "properties/images/" + propertyDirectory + "/" + UUID.randomUUID() + extension;
    }

    private String extractSafeExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "";
        }

        String filename = originalFilename.trim();
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "";
        }

        String extension = filename.substring(dotIndex + 1)
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]", "");
        if (extension.isBlank()) {
            return "";
        }

        return "." + extension;
    }
}
