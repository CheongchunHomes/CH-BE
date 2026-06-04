package com.chcorp.homes.alarm.entity;

import com.chcorp.homes.common.entity.MutableBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "alarm_notifications")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AlarmNotification extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_notification_id")
    private Long alarmNotificationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Setter
    @Column(name = "checked", nullable = false)
    private boolean checked;

    public void markAsRead() {
        this.checked = true;
    }
}
