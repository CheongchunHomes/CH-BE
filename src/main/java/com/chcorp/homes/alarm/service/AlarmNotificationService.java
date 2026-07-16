package com.chcorp.homes.alarm.service;

import com.chcorp.homes.alarm.dto.AlarmNotificationResponseDTO;
import com.chcorp.homes.alarm.entity.AlarmNotification;
import com.chcorp.homes.alarm.repository.AlarmNotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlarmNotificationService {

    private static final String LOAN_APPROVED_MESSAGE = "대출신청이 완료되었습니다";

    private final AlarmNotificationRepository alarmNotificationRepository;

    @Transactional
    public void createLoanApprovedAlarm(Long userId) {
        alarmNotificationRepository.save(AlarmNotification.builder()
                .userId(userId)
                .description(LOAN_APPROVED_MESSAGE)
                .checked(false)
                .build());
    }

    @Transactional(readOnly = true)
    public List<AlarmNotificationResponseDTO> getUnreadAlarms(Long userId) {
        return alarmNotificationRepository.findAllByUserIdAndCheckedFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(AlarmNotificationResponseDTO::from)
                .toList();
    }

    @Transactional
    public void markAsRead(Long alarmNotificationId, Long userId) {
        AlarmNotification alarmNotification = alarmNotificationRepository.findByAlarmNotificationIdAndUserId(alarmNotificationId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Alarm notification not found: " + alarmNotificationId));

        alarmNotification.markAsRead();
        alarmNotificationRepository.save(alarmNotification);
    }
}
