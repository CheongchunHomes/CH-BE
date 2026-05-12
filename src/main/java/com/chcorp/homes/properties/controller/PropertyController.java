package com.chcorp.homes.properties.controller;

import com.chcorp.homes.properties.dto.PropertyDTO;
import com.chcorp.homes.properties.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    // 지도에 표시할 매물 목록을 조회합니다.
    @GetMapping("/map")
    public List<PropertyDTO> getMapProperties() {
        return propertyService.getMapProperties();
    }

    // 매물 상세 정보를 조회합니다.
    @GetMapping("/{propertyId}")
    public PropertyDTO getProperty(@PathVariable Long propertyId) {
        return propertyService.getProperty(propertyId);
    }
}