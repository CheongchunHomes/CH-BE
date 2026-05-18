package com.chcorp.homes.mapchat.repository;

import com.chcorp.homes.mapchat.entity.MapChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MapChatRoomRepository extends JpaRepository<MapChatRoom, Long> {

    // 채팅방 중복 생성 방지용 조회
    Optional<MapChatRoom> findByPropertyIdAndTenantUserIdAndLandlordUserId(
            Long propertyId,
            Long tenantUserId,
            Long landlordUserId
    );

    // 임차인 내 문의 목록 조회용
    List<MapChatRoom> findByTenantUserIdOrderByLastMessageAtDesc(Long tenantUserId);

    // 임대인에게 온 전체 문의 채팅방 조회용
    List<MapChatRoom> findByLandlordUserIdOrderByLastMessageAtDesc(Long landlordUserId);

    // 임대인 특정 매물의 채팅방 목록 조회용
    List<MapChatRoom> findByPropertyIdAndLandlordUserIdOrderByLastMessageAtDesc(
            Long propertyId,
            Long landlordUserId
    );
}