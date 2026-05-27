package com.chcorp.homes.announcements.repository;

import com.chcorp.homes.announcements.entity.Announcement;
import com.chcorp.homes.announcements.entity.QAnnouncement;
import com.chcorp.homes.subscription.entity.QSubscriptionHouseType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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
            BigDecimal latitude,
            BigDecimal longitude,
            String locationFilter,
            String areaType,
            int page,
            int size
    ) {
        QAnnouncement announcement = QAnnouncement.announcement;

        QSubscriptionHouseType houseType = QSubscriptionHouseType.subscriptionHouseType;

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

        // =========================
        // 전용면적 필터
        // subscription_house_types.exclusive_area 기준
        // =========================
        if (hasText(areaType) && !"전체".equals(areaType)) {
            NumberExpression<Double> exclusiveAreaExpression = Expressions.numberTemplate(
                    Double.class,
                    "cast({0} as double)",
                    houseType.exclusiveArea
            );

            BooleanBuilder areaCondition = new BooleanBuilder();

            if ("39㎡ 이하".equals(areaType)) {
                areaCondition.and(exclusiveAreaExpression.loe(39.0));
            }

            if ("40~59㎡".equals(areaType) || "40-59㎡".equals(areaType)) {
                areaCondition.and(exclusiveAreaExpression.goe(40.0));
                areaCondition.and(exclusiveAreaExpression.loe(59.0));
            }

            if ("60~84㎡".equals(areaType) || "60-84㎡".equals(areaType)) {
                areaCondition.and(exclusiveAreaExpression.goe(60.0));
                areaCondition.and(exclusiveAreaExpression.loe(84.0));
            }

            if ("85㎡ 이상".equals(areaType)) {
                areaCondition.and(exclusiveAreaExpression.goe(85.0));
            }

            if (areaCondition.hasValue()) {
                condition.and(
                        JPAExpressions
                                .selectOne()
                                .from(houseType)
                                .where(
                                        houseType.announcementId.eq(announcement.announcementId),
                                        houseType.exclusiveArea.isNotNull(),
                                        houseType.exclusiveArea.isNotEmpty(),
                                        areaCondition
                                )
                                .exists()
                );
            }
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