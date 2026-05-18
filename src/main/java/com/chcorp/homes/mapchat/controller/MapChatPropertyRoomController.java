package com.chcorp.homes.mapchat.controller;

import com.chcorp.homes.mapchat.dto.mapChatRoom.MapChatRoomListResponseDTO;
import com.chcorp.homes.mapchat.service.MapChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 매물 기준 문의 채팅방 목록 API
 *
 * 프론트 호출 기준:
 * GET /api/map/chat/properties/{propertyId}/rooms
 *
 * 백엔드 실제 매핑:
 * GET /map/chat/properties/{propertyId}/rooms
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/map/chat/properties")
public class MapChatPropertyRoomController {

    private final MapChatRoomService mapChatRoomService;

    /**
     * 특정 매물에 들어온 문의 채팅방 목록 조회
     */
    @GetMapping("/{propertyId}/rooms")
    public ResponseEntity<List<MapChatRoomListResponseDTO>> getPropertyChatRooms(
            Authentication authentication,
            @PathVariable Long propertyId
    ) {
        Long currentUserId = Long.valueOf(authentication.getName());

        List<MapChatRoomListResponseDTO> response =
                mapChatRoomService.getPropertyChatRooms(
                        currentUserId,
                        propertyId
                );

        return ResponseEntity.ok(response);
    }
}