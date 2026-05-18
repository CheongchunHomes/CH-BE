package com.chcorp.homes.mapchat.repository;

import com.chcorp.homes.mapchat.entity.MapChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MapChatMessageRepository extends JpaRepository<MapChatMessage, Long> {

    // 채팅방 기존 메시지 조회
    List<MapChatMessage> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);

    // 특정 사용자의 전체 안 읽은 메시지 수
    long countByReceiverIdAndReadFalse(Long receiverId);

    // 특정 채팅방에서 현재 사용자가 안 읽은 메시지 수
    long countByChatRoomIdAndReceiverIdAndReadFalse(Long chatRoomId, Long receiverId);

    // 채팅방 입장 시 읽음 처리 대상 메시지 조회
    List<MapChatMessage> findByChatRoomIdAndReceiverIdAndReadFalse(
            Long chatRoomId,
            Long receiverId
    );
}