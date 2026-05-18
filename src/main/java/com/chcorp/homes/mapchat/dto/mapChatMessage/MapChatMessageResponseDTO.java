package com.chcorp.homes.mapchat.dto.mapChatMessage;

import com.chcorp.homes.mapchat.entity.MapChatMessage;
import com.chcorp.homes.mapchat.entity.MapChatMessageType;

import java.time.Instant;

public record MapChatMessageResponseDTO(
        Long messageId,
        Long chatRoomId,
        Long senderId,
        Long receiverId,
        String messageContent,
        MapChatMessageType messageType,
        boolean read,
        boolean mine,
        Instant createdAt
) {
    public static MapChatMessageResponseDTO from(
            MapChatMessage message,
            Long currentUserId
    ) {
        boolean mine = message.getSenderId().equals(currentUserId);

        return new MapChatMessageResponseDTO(
                message.getId(),
                message.getChatRoomId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getMessageContent(),
                message.getMessageType(),
                message.isRead(),
                mine,
                message.getCreatedAt()
        );
    }
}