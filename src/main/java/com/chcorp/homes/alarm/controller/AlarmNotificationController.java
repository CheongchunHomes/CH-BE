package com.chcorp.homes.alarm.controller;

import com.chcorp.homes.alarm.dto.AlarmNotificationResponseDTO;
import com.chcorp.homes.alarm.service.AlarmNotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alarm-notifications")
public class AlarmNotificationController {

    private final AlarmNotificationService alarmNotificationService;

    @GetMapping("/unread")
    public ResponseEntity<List<AlarmNotificationResponseDTO>> getUnreadAlarms(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.ok(List.of());
        }
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(alarmNotificationService.getUnreadAlarms(userId));
    }

    @PostMapping("/{alarmNotificationId}/read")
    public ResponseEntity<Void> markAsRead(
            Authentication authentication,
            @PathVariable Long alarmNotificationId
    ) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.noContent().build();
        }
        Long userId = Long.valueOf(authentication.getName());
        alarmNotificationService.markAsRead(alarmNotificationId, userId);
        return ResponseEntity.noContent().build();
    }
}
