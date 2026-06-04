package com.chcorp.homes.alarm.dto;

import com.chcorp.homes.alarm.entity.AlarmNotification;
import java.time.Instant;

public record AlarmNotificationResponseDTO(
        Long alarmNotificationId,
        Long userId,
        String description,
        boolean checked,
        Instant createdAt,
        Instant updatedAt
) {
    public static AlarmNotificationResponseDTO from(AlarmNotification alarmNotification) {
        return new AlarmNotificationResponseDTO(
                alarmNotification.getAlarmNotificationId(),
                alarmNotification.getUserId(),
                alarmNotification.getDescription(),
                alarmNotification.isChecked(),
                alarmNotification.getCreatedAt(),
                alarmNotification.getUpdatedAt()
        );
    }
}
