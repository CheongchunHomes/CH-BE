package com.chcorp.homes.mapchat.service;

import com.chcorp.homes.mapchat.dto.mapChatRoom.MapChatRoomCreateRequestDTO;
import com.chcorp.homes.mapchat.dto.mapChatRoom.MapChatRoomResponseDTO;
import com.chcorp.homes.mapchat.entity.MapChatRoom;
import com.chcorp.homes.mapchat.repository.MapChatRoomRepository;
import com.chcorp.homes.properties.entity.Property;
import com.chcorp.homes.properties.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import com.chcorp.homes.mapchat.dto.mapChatRoom.MapChatRoomListResponseDTO;
import com.chcorp.homes.mapchat.repository.MapChatMessageRepository;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MapChatRoomService {

    private final MapChatRoomRepository mapChatRoomRepository;
    private final PropertyRepository propertyRepository;
    private final MapChatMessageRepository mapChatMessageRepository;

    /**
     * 매물 상세페이지에서 문의하기 클릭 시
     * 기존 채팅방이 있으면 기존 방 반환,
     * 없으면 새 채팅방을 생성합니다.
     */
    @Transactional
    public MapChatRoomResponseDTO createOrGetRoom(
            Long currentUserId,
            MapChatRoomCreateRequestDTO request
    ) {
        validateRequest(currentUserId, request);

        Property property = propertyRepository.findById(request.propertyId())
                .orElseThrow(() -> new NoSuchElementException("매물을 찾을 수 없습니다."));

        Long landlordUserId = property.getLandlordUserId();

        if (landlordUserId == null) {
            throw new IllegalStateException("이 매물에는 임대인 정보가 없습니다.");
        }

        if (currentUserId.equals(landlordUserId)) {
            throw new IllegalStateException("본인 매물에는 문의할 수 없습니다.");
        }

        Long tenantUserId = currentUserId;

        MapChatRoom chatRoom = mapChatRoomRepository
                .findByPropertyIdAndTenantUserIdAndLandlordUserId(
                        property.getId(),
                        tenantUserId,
                        landlordUserId
                )
                .orElseGet(() -> mapChatRoomRepository.save(
                        MapChatRoom.create(
                                property.getId(),
                                tenantUserId,
                                landlordUserId
                        )
                ));

        return MapChatRoomResponseDTO.from(chatRoom);
    }

    /**
     * 요청값 검증
     */
    private void validateRequest(Long currentUserId, MapChatRoomCreateRequestDTO request) {
        if (currentUserId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        if (request == null || request.propertyId() == null) {
            throw new IllegalArgumentException("propertyId가 필요합니다.");
        }
    }
    /**
     * 내 채팅방 목록 조회
     */
    @Transactional(readOnly = true)
    public List<MapChatRoomListResponseDTO> getMyChatRooms(Long currentUserId) {
        if (currentUserId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        // 내가 임차인으로 참여한 채팅방
        List<MapChatRoom> tenantRooms =
                mapChatRoomRepository.findByTenantUserIdOrderByLastMessageAtDesc(currentUserId);

        // 내가 임대인으로 참여한 채팅방
        List<MapChatRoom> landlordRooms =
                mapChatRoomRepository.findByLandlordUserIdOrderByLastMessageAtDesc(currentUserId);

        // 두 목록 합치기 + 중복 제거
        Map<Long, MapChatRoom> roomMap = new LinkedHashMap<>();

        Stream.concat(tenantRooms.stream(), landlordRooms.stream())
                .forEach(room -> roomMap.put(room.getId(), room));

        return roomMap.values()
                .stream()
                .sorted((room1, room2) -> {
                    Instant time1 = getSortTime(room1);
                    Instant time2 = getSortTime(room2);

                    return time2.compareTo(time1);
                })
                .map(chatRoom -> {
                    long unreadCount =
                            mapChatMessageRepository.countByChatRoomIdAndReceiverIdAndReadFalse(
                                    chatRoom.getId(),
                                    currentUserId
                            );

                    return MapChatRoomListResponseDTO.from(
                            chatRoom,
                            currentUserId,
                            unreadCount
                    );
                })
                .toList();
    }

    /**
     * 정렬 기준 시간
     *
     * 마지막 메시지가 있으면 lastMessageAt 기준,
     * 아직 메시지가 없으면 createdAt 기준으로 정렬
     */
    private Instant getSortTime(MapChatRoom chatRoom) {
        if (chatRoom.getLastMessageAt() != null) {
            return chatRoom.getLastMessageAt();
        }

        return chatRoom.getCreatedAt();
    }

    /**
     * 특정 매물에 들어온 문의 채팅방 목록 조회
     *
     * 임대인 본인 매물일 때만 조회 가능
     */
    @Transactional(readOnly = true)
    public List<MapChatRoomListResponseDTO> getPropertyChatRooms(
            Long currentUserId,
            Long propertyId
    ) {
        if (currentUserId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        if (propertyId == null) {
            throw new IllegalArgumentException("propertyId가 필요합니다.");
        }

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new NoSuchElementException("매물을 찾을 수 없습니다."));

        Long landlordUserId = property.getLandlordUserId();

        if (landlordUserId == null) {
            throw new IllegalStateException("해당 매물에는 임대인 정보가 없습니다.");
        }

        if (!landlordUserId.equals(currentUserId)) {
            throw new IllegalStateException("해당 매물의 문의 목록을 조회할 권한이 없습니다.");
        }

        List<MapChatRoom> chatRooms =
                mapChatRoomRepository.findByPropertyIdAndLandlordUserIdOrderByLastMessageAtDesc(
                        propertyId,
                        currentUserId
                );

        return chatRooms.stream()
                .sorted((room1, room2) -> {
                    Instant time1 = getSortTime(room1);
                    Instant time2 = getSortTime(room2);

                    return time2.compareTo(time1);
                })
                .map(chatRoom -> {
                    long unreadCount =
                            mapChatMessageRepository.countByChatRoomIdAndReceiverIdAndReadFalse(
                                    chatRoom.getId(),
                                    currentUserId
                            );

                    return MapChatRoomListResponseDTO.from(
                            chatRoom,
                            currentUserId,
                            unreadCount
                    );
                })
                .toList();
    }

}