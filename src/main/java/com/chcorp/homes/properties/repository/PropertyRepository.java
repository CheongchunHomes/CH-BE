package com.chcorp.homes.properties.repository;

import com.chcorp.homes.properties.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {

    // 지도에 표시할 매물 조회
    List<Property> findByLatitudeIsNotNullAndLongitudeIsNotNull();

    // 현재 로그인 사용자가 등록한 매물이 있는지 확인
    boolean existsByLandlordUserId(Long landlordUserId);

    // 현재 로그인 사용자가 등록한 매물 목록 조회
    List<Property> findByLandlordUserIdOrderByCreatedAtDesc(Long landlordUserId);
}