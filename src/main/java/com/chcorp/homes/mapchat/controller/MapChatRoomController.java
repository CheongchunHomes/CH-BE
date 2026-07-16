package com.chcorp.homes.mapchat.controller;

import com.chcorp.homes.mapchat.dto.mapChatRoom.MapChatRoomCreateRequestDTO;
import com.chcorp.homes.mapchat.dto.mapChatRoom.MapChatRoomResponseDTO;
import com.chcorp.homes.mapchat.service.MapChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.chcorp.homes.mapchat.dto.mapChatRoom.MapChatRoomListResponseDTO;

import java.util.List;
/**
 * 매물 문의 채팅방 생성 또는 기존 채팅방 조회
 *
 * 프론트 호출 기준:
 * POST /api/map/chat/rooms
 *
 * 백엔드 실제 매핑:
 * POST /map/chat/rooms
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/map/chat/rooms")
public class MapChatRoomController {

    private final MapChatRoomService mapChatRoomService;

    @PostMapping
    public ResponseEntity<MapChatRoomResponseDTO> createOrGetRoom(
            Authentication authentication,
            @RequestBody MapChatRoomCreateRequestDTO request
    ) {
        Long currentUserId = Long.valueOf(authentication.getName());

        MapChatRoomResponseDTO response = mapChatRoomService.createOrGetRoom(
                currentUserId,
                request
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 내 채팅방 목록 조회
     */
    @GetMapping("/my")
    public ResponseEntity<List<MapChatRoomListResponseDTO>> getMyChatRooms(
            Authentication authentication
    ) {
        Long currentUserId = Long.valueOf(authentication.getName());

        List<MapChatRoomListResponseDTO> response =
                mapChatRoomService.getMyChatRooms(currentUserId);

        return ResponseEntity.ok(response);
    }
}