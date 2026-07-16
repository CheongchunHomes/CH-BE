package com.chcorp.homes.policies.repository;

import com.chcorp.homes.policies.entity.Policy;
import com.chcorp.homes.policies.entity.QPolicy;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PolicyQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Policy> search(
            String mainCategory,
            String subCategory,
            String region,
            String status,
            String supportType,
            String keyword,
            int page,
            int size
    ) {
        QPolicy policy = QPolicy.policy;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "policyId")
        );

        BooleanBuilder condition = new BooleanBuilder();

        // 사용자 화면에는 노출 제도만 조회
        condition.and(policy.isVisible.isTrue());

        if (hasText(mainCategory)) {
            condition.and(policy.mainCategory.eq(mainCategory));
        }

        if (hasText(subCategory)) {
            condition.and(policy.subCategory.eq(subCategory));
        }

        if (hasText(region)) {
            condition.and(policy.region.eq(region));
        }

        if (hasText(status)) {
            condition.and(policy.status.eq(status));
        }

        if (hasText(supportType)) {
            condition.and(policy.supportType.containsIgnoreCase(supportType));
        }

        if (hasText(keyword)) {
            condition.and(
                    policy.title.containsIgnoreCase(keyword)
                            .or(policy.region.containsIgnoreCase(keyword))
                            .or(policy.mainCategory.containsIgnoreCase(keyword))
                            .or(policy.subCategory.containsIgnoreCase(keyword))
                            .or(policy.originalCategory.containsIgnoreCase(keyword))
                            .or(policy.keyword.containsIgnoreCase(keyword))
                            .or(policy.summary.containsIgnoreCase(keyword))
                            .or(policy.content.containsIgnoreCase(keyword))
                            .or(policy.targetDesc.containsIgnoreCase(keyword))
                            .or(policy.supervisingInstitution.containsIgnoreCase(keyword))
            );
        }

        List<Policy> content = queryFactory
                .selectFrom(policy)
                .where(condition)
                .orderBy(policy.policyId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(policy.count())
                .from(policy)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isBlank();
    }
}