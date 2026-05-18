package com.chcorp.homes.mapchat.dto.mapChatRoom;

import com.chcorp.homes.mapchat.entity.MapChatRoom;

import java.time.Instant;

public record MapChatRoomResponseDTO(
        Long chatRoomId,
        Long propertyId,
        Long tenantUserId,
        Long landlordUserId,
        String lastMessageContent,
        Instant lastMessageAt,
        Long lastSenderId,
        Instant createdAt
) {
    public static MapChatRoomResponseDTO from(MapChatRoom chatRoom) {
        return new MapChatRoomResponseDTO(
                chatRoom.getId(),
                chatRoom.getPropertyId(),
                chatRoom.getTenantUserId(),
                chatRoom.getLandlordUserId(),
                chatRoom.getLastMessageContent(),
                chatRoom.getLastMessageAt(),
                chatRoom.getLastSenderId(),
                chatRoom.getCreatedAt()
        );
    }
}