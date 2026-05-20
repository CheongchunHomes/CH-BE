package com.chcorp.homes.announcements.repository;

import com.chcorp.homes.announcements.entity.Announcement;
import com.chcorp.homes.announcements.entity.QAnnouncement;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AnnouncementQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Announcement> search(
            String region,
            String status,
            String keyword,
            String sourceType,
            String targetType,
            boolean deadlineSoon,
            int page,
            int size
    ) {
        QAnnouncement announcement = QAnnouncement.announcement;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "applyEndDate")
        );

        BooleanBuilder condition = new BooleanBuilder();

        // 사용자 화면에는 노출 공고만 조회
        condition.and(announcement.isVisible.isTrue());

        if (hasText(region)) {
            condition.and(announcement.region.containsIgnoreCase(region));
        }

        if (hasText(status)) {
            condition.and(announcement.status.eq(status));
        }

        if (hasText(sourceType)) {
            condition.and(announcement.sourceType.eq(sourceType));
        }

        if (hasText(targetType)) {
            condition.and(announcement.targetType.eq(targetType));
        }

        if (deadlineSoon) {
            LocalDate today = LocalDate.now();
            LocalDate deadlineEnd = today.plusDays(30);

            condition.and(announcement.applyEndDate.between(today, deadlineEnd));
        }

        if (hasText(keyword)) {
            condition.and(
                    announcement.title.containsIgnoreCase(keyword)
                            .or(announcement.region.containsIgnoreCase(keyword))
                            .or(announcement.address.containsIgnoreCase(keyword))
                            .or(announcement.supplyInstitution.containsIgnoreCase(keyword))
                            .or(announcement.recuitmentType.containsIgnoreCase(keyword))
                            .or(announcement.targetType.containsIgnoreCase(keyword))
            );
        }

        List<Announcement> content = queryFactory
                .selectFrom(announcement)
                .where(condition)
                .orderBy(announcement.applyEndDate.desc().nullsLast(), announcement.announcementId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(announcement.count())
                .from(announcement)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isBlank();
    }
}

