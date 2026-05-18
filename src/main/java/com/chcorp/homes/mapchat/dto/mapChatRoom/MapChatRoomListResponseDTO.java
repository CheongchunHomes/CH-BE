package com.chcorp.homes.mapchat.dto.mapChatRoom;

import com.chcorp.homes.mapchat.entity.MapChatRoom;

import java.time.Instant;

public record MapChatRoomListResponseDTO(
        Long chatRoomId,
        Long propertyId,
        Long tenantUserId,
        Long landlordUserId,
        String lastMessageContent,
        Instant lastMessageAt,
        Long lastSenderId,
        long unreadCount,
        String myRoleInRoom,
        Instant createdAt
) {
    public static MapChatRoomListResponseDTO from(
            MapChatRoom chatRoom,
            Long currentUserId,
            long unreadCount
    ) {
        return new MapChatRoomListResponseDTO(
                chatRoom.getId(),
                chatRoom.getPropertyId(),
                chatRoom.getTenantUserId(),
                chatRoom.getLandlordUserId(),
                chatRoom.getLastMessageContent(),
                chatRoom.getLastMessageAt(),
                chatRoom.getLastSenderId(),
                unreadCount,
                resolveMyRoleInRoom(chatRoom, currentUserId),
                chatRoom.getCreatedAt()
        );
    }

    private static String resolveMyRoleInRoom(
            MapChatRoom chatRoom,
            Long currentUserId
    ) {
        if (chatRoom.getTenantUserId().equals(currentUserId)) {
            return "TENANT";
        }

        if (chatRoom.getLandlordUserId().equals(currentUserId)) {
            return "LANDLORD";
        }

        return "UNKNOWN";
    }
}