package com.chcorp.homes.alarm.repository;

import com.chcorp.homes.alarm.entity.AlarmNotification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmNotificationRepository extends JpaRepository<AlarmNotification, Long> {

    java.util.Optional<AlarmNotification> findByAlarmNotificationIdAndUserId(Long alarmNotificationId, Long userId);

    List<AlarmNotification> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    List<AlarmNotification> findAllByUserIdAndCheckedFalseOrderByCreatedAtDesc(Long userId);

    List<AlarmNotification> findAllByUserIdAndCheckedTrueOrderByCreatedAtDesc(Long userId);
}
