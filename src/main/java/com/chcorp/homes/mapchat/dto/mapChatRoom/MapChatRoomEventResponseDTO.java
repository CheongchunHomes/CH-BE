package com.chcorp.homes.mapchat.dto.mapChatRoom;

public record MapChatRoomEventResponseDTO(
        MapChatRoomListResponseDTO room,
        long totalUnreadCount
) {
    public static MapChatRoomEventResponseDTO of(
            MapChatRoomListResponseDTO room,
            long totalUnreadCount
    ) {
        return new MapChatRoomEventResponseDTO(room, totalUnreadCount);
    }
}