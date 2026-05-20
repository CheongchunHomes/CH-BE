package com.chcorp.homes.mapchat.controller;

import com.chcorp.homes.mapchat.dto.mapChatMessage.MapChatMessageListResponseDTO;
import com.chcorp.homes.mapchat.dto.mapChatMessage.MapChatMessageResponseDTO;
import com.chcorp.homes.mapchat.dto.mapChatMessage.MapChatMessageSendRequestDTO;
import com.chcorp.homes.mapchat.dto.mapChatMessage.MapChatReadEventResponseDTO;
import com.chcorp.homes.mapchat.service.MapChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
/**
 * 매물 문의 채팅 메시지 API
 *
 * 프론트 호출 기준:
 * GET  /api/map/chat/rooms/{chatRoomId}/messages
 * POST /api/map/chat/rooms/{chatRoomId}/messages
 *
 * 백엔드 실제 매핑:
 * GET  /map/chat/rooms/{chatRoomId}/messages
 * POST /map/chat/rooms/{chatRoomId}/messages
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/map/chat/rooms")
public class MapChatMessageController {

    private final MapChatMessageService mapChatMessageService;

    /**
     * 메시지 전송
     */
    @PostMapping("/{chatRoomId}/messages")
    public ResponseEntity<MapChatMessageResponseDTO> sendMessage(
            Authentication authentication,
            @PathVariable Long chatRoomId,
            @RequestBody MapChatMessageSendRequestDTO request
    ) {
        Long currentUserId = Long.valueOf(authentication.getName());

        MapChatMessageResponseDTO response =
                mapChatMessageService.sendMessage(
                        currentUserId,
                        chatRoomId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    /**
     * 메시지 목록 조회
     */
    @GetMapping("/{chatRoomId}/messages")
    public ResponseEntity<MapChatMessageListResponseDTO> getMessages(
            Authentication authentication,
            @PathVariable Long chatRoomId
    ) {
        Long currentUserId = Long.valueOf(authentication.getName());

        MapChatMessageListResponseDTO response =
                mapChatMessageService.getMessages(
                        currentUserId,
                        chatRoomId
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{chatRoomId}/read")
    public MapChatReadEventResponseDTO markMessagesAsRead(
            Authentication authentication,
            @PathVariable Long chatRoomId
    ) {
        Long currentUserId = Long.valueOf(authentication.getName());

        return mapChatMessageService.markMessagesAsRead(
                currentUserId,
                chatRoomId
        );
    }
}