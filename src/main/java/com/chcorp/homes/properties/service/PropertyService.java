package com.chcorp.homes.properties.service;

import com.chcorp.homes.properties.dto.PropertyDTO;
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

    // 지도에 표시할 매물 목록을 조회합니다.
    public List<PropertyDTO> getMapProperties() {
        return propertyRepository.findByLatitudeIsNotNullAndLongitudeIsNotNull()
                .stream()
                .map(PropertyDTO::from)
                .toList();
    }

    // 매물 상세 정보를 조회합니다.
    public PropertyDTO getProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new NoSuchElementException("매물을 찾을 수 없습니다."));

        return PropertyDTO.from(property);
    }
}