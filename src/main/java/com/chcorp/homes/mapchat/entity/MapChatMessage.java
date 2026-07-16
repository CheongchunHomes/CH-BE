package com.chcorp.homes.mapchat.entity;

import com.chcorp.homes.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "map_chat_messages",
        indexes = {
                @Index(name = "idx_map_chat_message_room_id", columnList = "chat_room_id"),
                @Index(name = "idx_map_chat_message_receiver_read", columnList = "receiver_id, is_read"),
                @Index(name = "idx_map_chat_message_created_at", columnList = "created_at")
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MapChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    // 채팅방 ID
    @Column(name = "chat_room_id", nullable = false)
    private Long chatRoomId;

    // 보낸 사람 ID
    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    // 받는 사람 ID
    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    // 메시지 내용
    @Column(name = "message_content", nullable = false, columnDefinition = "TEXT")
    private String messageContent;

    // 메시지 타입
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false, length = 30)
    private MapChatMessageType messageType;

    // 읽음 여부
    @Column(name = "is_read", nullable = false)
    private boolean read;

    // 일반 메시지 생성 메서드
    public static MapChatMessage createTextMessage(
            Long chatRoomId,
            Long senderId,
            Long receiverId,
            String messageContent
    ) {
        return MapChatMessage.builder()
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .receiverId(receiverId)
                .messageContent(messageContent)
                .messageType(MapChatMessageType.TEXT)
                .read(false)
                .build();
    }

    // 추후 SYSTEM, APPOINTMENT_REQUEST 메시지 확장 시 사용할 생성 메서드
    public static MapChatMessage create(
            Long chatRoomId,
            Long senderId,
            Long receiverId,
            String messageContent,
            MapChatMessageType messageType
    ) {
        return MapChatMessage.builder()
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .receiverId(receiverId)
                .messageContent(messageContent)
                .messageType(messageType == null ? MapChatMessageType.TEXT : messageType)
                .read(false)
                .build();
    }

    // 읽음 처리
    public void markAsRead() {
        this.read = true;
    }
}