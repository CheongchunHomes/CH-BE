package com.chcorp.homes.properties.repository;

import com.chcorp.homes.properties.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {

    List<Property> findByLatitudeIsNotNullAndLongitudeIsNotNull();

    boolean existsByLandlordUserId(Long landlordUserId);

    List<Property> findByLandlordUserIdOrderByCreatedAtDesc(Long landlordUserId);
}
