package com.chcorp.homes.mapchat.entity;

import com.chcorp.homes.common.entity.MutableBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "map_chat_rooms",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_map_chat_room_property_tenant_landlord",
                        columnNames = {"property_id", "tenant_user_id", "landlord_user_id"}
                )
        },
        indexes = {
                @Index(name = "idx_map_chat_room_property_id", columnList = "property_id"),
                @Index(name = "idx_map_chat_room_tenant_user_id", columnList = "tenant_user_id"),
                @Index(name = "idx_map_chat_room_landlord_user_id", columnList = "landlord_user_id"),
                @Index(name = "idx_map_chat_room_last_message_at", columnList = "last_message_at")
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MapChatRoom extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    // 문의 대상 매물 ID
    @Column(name = "property_id", nullable = false)
    private Long propertyId;

    // 문의한 사용자 ID
    @Column(name = "tenant_user_id", nullable = false)
    private Long tenantUserId;

    // 매물 등록자 ID
    @Column(name = "landlord_user_id", nullable = false)
    private Long landlordUserId;

    // 마지막 메시지 내용
    @Column(name = "last_message_content", columnDefinition = "TEXT")
    private String lastMessageContent;

    // 마지막 메시지 시간
    @Column(name = "last_message_at")
    private Instant lastMessageAt;

    // 마지막 메시지를 보낸 사용자 ID
    @Column(name = "last_sender_id")
    private Long lastSenderId;

    // 채팅방 생성 메서드
    public static MapChatRoom create(Long propertyId, Long tenantUserId, Long landlordUserId) {
        return MapChatRoom.builder()
                .propertyId(propertyId)
                .tenantUserId(tenantUserId)
                .landlordUserId(landlordUserId)
                .build();
    }

    // 마지막 메시지 정보 갱신
    public void updateLastMessage(String messageContent, Long senderId, Instant sentAt) {
        this.lastMessageContent = messageContent;
        this.lastSenderId = senderId;
        this.lastMessageAt = sentAt;
    }

    // 현재 사용자가 이 채팅방 참여자인지 확인
    public boolean isParticipant(Long userId) {
        if (userId == null) {
            return false;
        }

        return userId.equals(tenantUserId) || userId.equals(landlordUserId);
    }

    // 상대방 userId 구하기
    public Long getReceiverId(Long senderId) {
        if (senderId == null) {
            throw new IllegalArgumentException("senderId가 없습니다.");
        }

        if (senderId.equals(tenantUserId)) {
            return landlordUserId;
        }

        if (senderId.equals(landlordUserId)) {
            return tenantUserId;
        }

        throw new IllegalArgumentException("채팅방 참여자가 아닙니다.");
    }
}