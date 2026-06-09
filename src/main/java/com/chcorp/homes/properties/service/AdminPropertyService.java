package com.chcorp.homes.properties.service;

import com.chcorp.homes.files.service.FileService;
import com.chcorp.homes.properties.dto.AdminPropertyListDTO;
import com.chcorp.homes.properties.dto.AdminPropertyRequestDTO;
import com.chcorp.homes.properties.entity.Property;
import com.chcorp.homes.properties.repository.PropertyRepository;
import com.chcorp.homes.subscription.service.KakaoAddressGeocodingService;
import com.chcorp.homes.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminPropertyService {

    private static final int DEFAULT_PROPERTY_IMAGE_COUNT = 10;
    private static final String DEFAULT_PROPERTY_IMAGE_PATH = "/images/properties/default-%d.jpg";

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final KakaoAddressGeocodingService geocodingService;
    private final FileService fileService;

    @Transactional(readOnly = true)
    public Page<AdminPropertyListDTO> getList(String keyword, String category, String dealType, int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        return propertyRepository.findAll(buildSearchSpec(keyword, category, dealType), pageable)
                .map(property -> AdminPropertyListDTO.from(
                        property,
                        resolveThumbnailPreviewUrl(property.getId(), property.getThumbnailUrl())
                ));
    }

    @Transactional(readOnly = true)
    public Property getOne(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("물건을 찾을 수 없습니다."));
    }

    @Transactional
    public void register(AdminPropertyRequestDTO dto) {
        validate(dto);

        KakaoAddressGeocodingService.Coordinates coordinates = resolveCoordinates(dto.getAddress());
        String thumbnailUrl = resolveThumbnailUrl(dto.getThumbnailUrl());

        Property property = Property.builder()
                .title(dto.getTitle().trim())
                .address(dto.getAddress().trim())
                .region(normalize(dto.getRegion()))
                .landlordUserId(dto.getLandlordUserId())
                .latitude(coordinates.latitude().doubleValue())
                .longitude(coordinates.longitude().doubleValue())
                .category(normalize(dto.getCategory()))
                .dealType(normalize(dto.getDealType()))
                .depositAmount(dto.getDepositAmount())
                .monthlyRentAmount(dto.getMonthlyRentAmount())
                .maintenanceFee(dto.getMaintenanceFee())
                .roomType(normalize(dto.getRoomType()))
                .exclusiveAreaM2(dto.getExclusiveAreaM2())
                .supplyAreaM2(dto.getSupplyAreaM2())
                .roomCount(dto.getRoomCount())
                .bathroomCount(dto.getBathroomCount())
                .floor(dto.getFloor())
                .totalFloor(dto.getTotalFloor())
                .direction(normalize(dto.getDirection()))
                .heatingType(normalize(dto.getHeatingType()))
                .elevatorAvailable(Boolean.TRUE.equals(dto.getElevatorAvailable()))
                .totalParkingCount(dto.getTotalParkingCount())
                .buildingUse(normalize(dto.getBuildingUse()))
                .moveInType(normalize(dto.getMoveInType()))
                .moveInDate(dto.getMoveInDate())
                .approvalDate(dto.getApprovalDate())
                .firstRegistrationDate(dto.getFirstRegistrationDate())
                .tag(normalizeCommaText(dto.getTag()))
                .options(normalizeCommaText(dto.getOptions()))
                .securityFacilities(normalizeCommaText(dto.getSecurityFacilities()))
                .thumbnailUrl(thumbnailUrl)
                .description(normalize(dto.getDescription()))
                .build();

        propertyRepository.save(property);
    }

    @Transactional
    public void update(Long id, AdminPropertyRequestDTO dto) {
        validate(dto);

        Property property = getOne(id);
        KakaoAddressGeocodingService.Coordinates coordinates = resolveCoordinatesForUpdate(property, dto.getAddress());
        String thumbnailUrl = resolveThumbnailUrl(dto.getThumbnailUrl());

        property.updateAdminFields(
                dto.getTitle().trim(),
                dto.getAddress().trim(),
                normalize(dto.getRegion()),
                dto.getLandlordUserId(),
                coordinates.latitude().doubleValue(),
                coordinates.longitude().doubleValue(),
                normalize(dto.getCategory()),
                normalize(dto.getDealType()),
                dto.getDepositAmount(),
                dto.getMonthlyRentAmount(),
                dto.getMaintenanceFee(),
                normalize(dto.getRoomType()),
                dto.getExclusiveAreaM2(),
                dto.getSupplyAreaM2(),
                dto.getRoomCount(),
                dto.getBathroomCount(),
                dto.getFloor(),
                dto.getTotalFloor(),
                normalize(dto.getDirection()),
                normalize(dto.getHeatingType()),
                Boolean.TRUE.equals(dto.getElevatorAvailable()),
                dto.getTotalParkingCount(),
                normalize(dto.getBuildingUse()),
                normalize(dto.getMoveInType()),
                dto.getMoveInDate(),
                dto.getApprovalDate(),
                dto.getFirstRegistrationDate(),
                normalizeCommaText(dto.getTag()),
                normalizeCommaText(dto.getOptions()),
                normalizeCommaText(dto.getSecurityFacilities()),
                thumbnailUrl,
                normalize(dto.getDescription())
        );
    }

    @Transactional
    public void delete(Long id) {
        Property property = getOne(id);
        propertyRepository.delete(property);
    }

    @Transactional(readOnly = true)
    public String resolveThumbnailPreviewUrl(String thumbnailUrl) {
        return resolveThumbnailPreviewUrl(null, thumbnailUrl);
    }

    @Transactional(readOnly = true)
    public String resolveThumbnailPreviewUrl(Long propertyId, String thumbnailUrl) {
        String normalizedThumbnailUrl = normalize(thumbnailUrl);
        if (normalizedThumbnailUrl == null) {
            return resolveFallbackThumbnailUrl(propertyId);
        }

        Long fileId = fileService.parseFileReference(thumbnailUrl);
        if (fileId == null) {
            return normalizedThumbnailUrl;
        }

        try {
            return fileService.createPublicSignedDownloadUrl(fileId).signedUrl();
        } catch (RuntimeException e) {
            return resolveFallbackThumbnailUrl(propertyId);
        }
    }

    private String resolveFallbackThumbnailUrl(Long propertyId) {
        long seed = propertyId == null ? 0L : propertyId - 1L;
        int imageNumber = Math.floorMod(seed, DEFAULT_PROPERTY_IMAGE_COUNT) + 1;
        return DEFAULT_PROPERTY_IMAGE_PATH.formatted(imageNumber);
    }

    private void validate(AdminPropertyRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("물건 등록 정보가 없습니다.");
        }
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new IllegalArgumentException("물건명을 입력해 주세요.");
        }
        if (dto.getAddress() == null || dto.getAddress().isBlank()) {
            throw new IllegalArgumentException("주소를 입력해 주세요.");
        }
        if (dto.getLandlordUserId() == null) {
            throw new IllegalArgumentException("임대인 ID를 입력해 주세요.");
        }
        if (!userRepository.existsById(dto.getLandlordUserId())) {
            throw new IllegalArgumentException("존재하지 않는 임대인 ID입니다.");
        }
    }

    private KakaoAddressGeocodingService.Coordinates resolveCoordinates(String address) {
        return geocodingService.geocode(address)
                .orElseThrow(() -> new IllegalArgumentException("주소를 위도/경도로 변환할 수 없습니다."));
    }

    private KakaoAddressGeocodingService.Coordinates resolveCoordinatesForUpdate(Property property, String address) {
        if (!isAddressChanged(property.getAddress(), address)
                && property.getLatitude() != null
                && property.getLongitude() != null) {
            return new KakaoAddressGeocodingService.Coordinates(
                    BigDecimal.valueOf(property.getLatitude()),
                    BigDecimal.valueOf(property.getLongitude())
            );
        }

        return resolveCoordinates(address);
    }

    private boolean isAddressChanged(String currentAddress, String nextAddress) {
        String current = normalize(currentAddress);
        String next = normalize(nextAddress);

        if (current == null) {
            return next != null;
        }

        return !current.equals(next);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private String normalizeCommaText(String value) {
        String normalized = normalize(value);
        if (normalized == null) {
            return null;
        }

        return normalized.replaceAll("\\s*,\\s*", ",");
    }

    private String resolveThumbnailUrl(String thumbnailUrl) {
        return normalize(thumbnailUrl);
    }

    private Specification<Property> buildSearchSpec(String keyword, String category, String dealType) {
        String normalizedKeyword = normalize(keyword);
        String normalizedCategory = normalize(category);
        String normalizedDealType = normalize(dealType);

        return (root, query, criteriaBuilder) -> {
            var predicate = criteriaBuilder.conjunction();

            if (normalizedKeyword != null) {
                String likeKeyword = "%" + normalizedKeyword.toLowerCase() + "%";
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), likeKeyword),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), likeKeyword)
                        )
                );
            }

            if (normalizedCategory != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(root.get("category"), normalizedCategory)
                );
            }

            if (normalizedDealType != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(root.get("dealType"), normalizedDealType)
                );
            }

            return predicate;
        };
    }
}
