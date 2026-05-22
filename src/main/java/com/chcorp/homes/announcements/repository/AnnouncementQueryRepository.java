package com.chcorp.homes.announcements.repository;

import com.chcorp.homes.announcements.entity.Announcement;
import com.chcorp.homes.announcements.entity.QAnnouncement;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
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
            Double latitude,
            Double longitude,
            String locationFilter,
            int page,
            int size
    ) {
        QAnnouncement announcement = QAnnouncement.announcement;

        Pageable pageable = PageRequest.of(page, size);

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

        boolean useLocationFilter =
                latitude != null
                        && longitude != null
                        && hasText(locationFilter)
                        && !"전체".equals(locationFilter);

        NumberExpression<Double> distanceExpression = null;

        if (useLocationFilter) {
            // 위치 기반 필터를 적용할 때는 좌표가 있는 공고만 조회
            condition.and(announcement.latitude.isNotNull());
            condition.and(announcement.longitude.isNotNull());

            // Haversine 공식 기반 거리 계산
            // 결과 단위: km
            distanceExpression = Expressions.numberTemplate(
                    Double.class,
                    """
                    (6371 * acos(
                        cos(radians({0})) *
                        cos(radians({1})) *
                        cos(radians({2}) - radians({3})) +
                        sin(radians({0})) *
                        sin(radians({1}))
                    ))
                    """,
                    latitude,
                    announcement.latitude,
                    announcement.longitude,
                    longitude
            );

            if ("5km 이내".equals(locationFilter)) {
                condition.and(distanceExpression.loe(5.0));
            }

            if ("10km 이내".equals(locationFilter)) {
                condition.and(distanceExpression.loe(10.0));
            }
        }

        JPAQuery<Announcement> query = queryFactory
                .selectFrom(announcement)
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (useLocationFilter && distanceExpression != null) {
            // 거리 순 / 5km 이내 / 10km 이내 모두 거리순 정렬
            query.orderBy(
                    distanceExpression.asc(),
                    announcement.applyEndDate.desc().nullsLast(),
                    announcement.announcementId.desc()
            );
        } else {
            query.orderBy(
                    announcement.applyEndDate.desc().nullsLast(),
                    announcement.announcementId.desc()
            );
        }

        List<Announcement> content = query.fetch();

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