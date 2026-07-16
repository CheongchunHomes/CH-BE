package com.chcorp.homes.mapchat.service;

import com.chcorp.homes.mapchat.dto.mapChatMessage.MapChatMessageListResponseDTO;
import com.chcorp.homes.mapchat.dto.mapChatMessage.MapChatMessageResponseDTO;
import com.chcorp.homes.mapchat.dto.mapChatMessage.MapChatMessageSendRequestDTO;
import com.chcorp.homes.mapchat.dto.mapChatMessage.MapChatReadEventResponseDTO;
import com.chcorp.homes.mapchat.entity.MapChatMessage;
import com.chcorp.homes.mapchat.entity.MapChatRoom;
import com.chcorp.homes.mapchat.repository.MapChatMessageRepository;
import com.chcorp.homes.mapchat.repository.MapChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import lombok.extern.slf4j.Slf4j;

import com.chcorp.homes.mapchat.dto.mapChatRoom.MapChatRoomEventResponseDTO;
import com.chcorp.homes.mapchat.dto.mapChatRoom.MapChatRoomListResponseDTO;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapChatMessageService {

    private final MapChatRoomRepository mapChatRoomRepository;
    private final MapChatMessageRepository mapChatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 메시지 전송
     */
    @Transactional
    public MapChatMessageResponseDTO sendMessage(
            Long currentUserId,
            Long chatRoomId,
            MapChatMessageSendRequestDTO request
    ) {
        validateSendRequest(currentUserId, chatRoomId, request);

        MapChatRoom chatRoom = mapChatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NoSuchElementException("채팅방을 찾을 수 없습니다."));

        if (!chatRoom.isParticipant(currentUserId)) {
            throw new IllegalStateException("해당 채팅방에 접근할 권한이 없습니다.");
        }

        Long senderId = currentUserId;
        Long receiverId = chatRoom.getReceiverId(senderId);

        MapChatMessage message = MapChatMessage.createTextMessage(
                chatRoom.getId(),
                senderId,
                receiverId,
                request.messageContent().trim()
        );

        MapChatMessage savedMessage = mapChatMessageRepository.save(message);

        Instant sentAt = Instant.now();

        chatRoom.updateLastMessage(
                savedMessage.getMessageContent(),
                senderId,
                sentAt
        );

        runAfterCommit(() -> {
            sendMessageToSubscribers(savedMessage, senderId, receiverId);

            // 메시지 전송 후 양쪽 사용자 채팅 목록을 실시간 갱신합니다.
            sendRoomEventToUser(chatRoom, senderId);
            sendRoomEventToUser(chatRoom, receiverId);
        });

        return MapChatMessageResponseDTO.from(savedMessage, senderId);
    }

    /**
     * 채팅방 메시지 목록 조회
     *
     * 주의:
     * 이 메서드는 메시지 조회만 담당합니다.
     * 읽음 처리는 POST /read 요청에서 markMessagesAsRead()가 담당합니다.
     */
    @Transactional(readOnly = true)
    public MapChatMessageListResponseDTO getMessages(
            Long currentUserId,
            Long chatRoomId
    ) {
        validateMessageListRequest(currentUserId, chatRoomId);

        MapChatRoom chatRoom = mapChatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NoSuchElementException("채팅방을 찾을 수 없습니다."));

        if (!chatRoom.isParticipant(currentUserId)) {
            throw new IllegalStateException("해당 채팅방에 접근할 권한이 없습니다.");
        }

        List<MapChatMessageResponseDTO> messages =
                mapChatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId)
                        .stream()
                        .map(message -> MapChatMessageResponseDTO.from(message, currentUserId))
                        .toList();

        return MapChatMessageListResponseDTO.of(
                chatRoomId,
                messages
        );
    }

    /**
     * 구독 중인 양쪽 사용자에게 새 메시지를 전송합니다.
     */
    private void sendMessageToSubscribers(
            MapChatMessage savedMessage,
            Long senderId,
            Long receiverId
    ) {
        Long chatRoomId = savedMessage.getChatRoomId();

        MapChatMessageResponseDTO senderResponse =
                MapChatMessageResponseDTO.from(savedMessage, senderId);

        MapChatMessageResponseDTO receiverResponse =
                MapChatMessageResponseDTO.from(savedMessage, receiverId);

        messagingTemplate.convertAndSend(
                "/sub/map/chat/rooms/" + chatRoomId + "/users/" + senderId,
                senderResponse
        );

        messagingTemplate.convertAndSend(
                "/sub/map/chat/rooms/" + chatRoomId + "/users/" + receiverId,
                receiverResponse
        );
    }

    /**
     * 메시지 전송 요청값 검증
     */
    private void validateSendRequest(
            Long currentUserId,
            Long chatRoomId,
            MapChatMessageSendRequestDTO request
    ) {
        if (currentUserId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        if (chatRoomId == null) {
            throw new IllegalArgumentException("chatRoomId가 필요합니다.");
        }

        if (request == null || request.messageContent() == null || request.messageContent().trim().isBlank()) {
            throw new IllegalArgumentException("메시지 내용을 입력해주세요.");
        }
    }

    /**
     * 메시지 목록 조회 요청값 검증
     */
    private void validateMessageListRequest(Long currentUserId, Long chatRoomId) {
        if (currentUserId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        if (chatRoomId == null) {
            throw new IllegalArgumentException("chatRoomId가 필요합니다.");
        }
    }
    /**
     * 현재 사용자가 받은 안 읽은 메시지를 읽음 처리하고 실시간 알림을 보냅니다.
     */
    @Transactional
    public MapChatReadEventResponseDTO markMessagesAsRead(
            Long currentUserId,
            Long chatRoomId
    ) {
        if (currentUserId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        if (chatRoomId == null) {
            throw new IllegalArgumentException("chatRoomId가 필요합니다.");
        }

        MapChatRoom chatRoom = mapChatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NoSuchElementException("채팅방을 찾을 수 없습니다."));

        if (!chatRoom.isParticipant(currentUserId)) {
            throw new IllegalStateException("해당 채팅방에 접근할 권한이 없습니다.");
        }

        List<MapChatMessage> unreadMessages =
                mapChatMessageRepository.findByChatRoomIdAndReceiverIdAndReadFalse(
                        chatRoomId,
                        currentUserId
                );

        if (unreadMessages.isEmpty()) {
            return MapChatReadEventResponseDTO.of(
                    chatRoomId,
                    currentUserId,
                    List.of()
            );
        }

        unreadMessages.forEach(MapChatMessage::markAsRead);

        List<Long> readMessageIds = unreadMessages.stream()
                .map(MapChatMessage::getId)
                .toList();

        MapChatReadEventResponseDTO readEvent =
                MapChatReadEventResponseDTO.of(
                        chatRoomId,
                        currentUserId,
                        readMessageIds
                );

        Long otherUserId = chatRoom.getReceiverId(currentUserId);

        runAfterCommit(() -> {
            sendReadEventToSubscribers(readEvent, currentUserId, otherUserId);

            // 읽음 처리 후 양쪽 사용자 채팅 목록/안읽음 숫자를 실시간 갱신합니다.
            sendRoomEventToUser(chatRoom, currentUserId);
            sendRoomEventToUser(chatRoom, otherUserId);
        });

        return readEvent;
    }

    /**
     * 읽음 이벤트를 양쪽 사용자에게 전송합니다.
     */
    private void sendReadEventToSubscribers(
            MapChatReadEventResponseDTO readEvent,
            Long readerId,
            Long otherUserId
    ) {
        Long chatRoomId = readEvent.chatRoomId();
        log.info(
                "MAP_CHAT_READ_EVENT chatRoomId={} readerId={} otherUserId={} readMessageIds={}",
                chatRoomId,
                readerId,
                otherUserId,
                readEvent.readMessageIds()
        );

        messagingTemplate.convertAndSend(
                "/sub/map/chat/rooms/" + chatRoomId + "/users/" + readerId + "/read",
                readEvent
        );

        messagingTemplate.convertAndSend(
                "/sub/map/chat/rooms/" + chatRoomId + "/users/" + otherUserId + "/read",
                readEvent
        );
    }
    /**
     * 특정 사용자에게 채팅방 목록 갱신 이벤트를 전송합니다.
     */
    private void sendRoomEventToUser(MapChatRoom chatRoom, Long userId) {
        long totalUnreadCount =
                mapChatMessageRepository.countByReceiverIdAndReadFalse(userId);

        long roomUnreadCount =
                mapChatMessageRepository.countByChatRoomIdAndReceiverIdAndReadFalse(
                        chatRoom.getId(),
                        userId
                );

        MapChatRoomListResponseDTO room =
                MapChatRoomListResponseDTO.from(
                        chatRoom,
                        userId,
                        roomUnreadCount
                );

        MapChatRoomEventResponseDTO event =
                MapChatRoomEventResponseDTO.of(
                        room,
                        totalUnreadCount
                );

        messagingTemplate.convertAndSend(
                "/sub/map/chat/users/" + userId + "/rooms",
                event
        );
    }
    /**
     * 현재 트랜잭션이 commit 된 이후에 작업을 실행합니다.
     * DB 반영보다 WebSocket 이벤트가 먼저 나가는 문제를 방지합니다.
     */
    private void runAfterCommit(Runnable task) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            task.run();
                        }
                    }
            );
            return;
        }

        task.run();
    }
}