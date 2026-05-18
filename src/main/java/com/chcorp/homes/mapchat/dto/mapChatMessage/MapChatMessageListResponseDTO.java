package com.chcorp.homes.mapchat.dto.mapChatMessage;

import java.util.List;

public record MapChatMessageListResponseDTO(
        Long chatRoomId,
        List<MapChatMessageResponseDTO> messages
) {
    public static MapChatMessageListResponseDTO of(
            Long chatRoomId,
            List<MapChatMessageResponseDTO> messages
    ) {
        return new MapChatMessageListResponseDTO(
                chatRoomId,
                messages
        );
    }
}