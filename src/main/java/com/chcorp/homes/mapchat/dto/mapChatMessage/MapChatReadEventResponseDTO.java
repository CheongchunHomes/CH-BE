package com.chcorp.homes.mapchat.dto.mapChatMessage;

import java.time.Instant;
import java.util.List;

public record MapChatReadEventResponseDTO(
        Long chatRoomId,
        Long readerId,
        List<Long> readMessageIds,
        Instant readAt
) {
    public static MapChatReadEventResponseDTO of(
            Long chatRoomId,
            Long readerId,
            List<Long> readMessageIds
    ) {
        return new MapChatReadEventResponseDTO(
                chatRoomId,
                readerId,
                readMessageIds,
                Instant.now()
        );
    }
}