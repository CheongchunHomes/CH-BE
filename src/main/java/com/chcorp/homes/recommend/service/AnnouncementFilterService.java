package com.chcorp.homes.recommend.service;

import com.chcorp.homes.recommend.dto.RecommendCondition;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementFilterService {
    /*
    * TODO: announcement 테이블 연결 후 실제 구현
    * user_diagnosis_results 조건 vs announcements 조건 비교해서
    * 해당 유저에게 맞는 공고만 필터링
    * */
    public void filterAnnouncements(RecommendCondition condition) {

    // TODO: 아래 순서로 구현 예정
    // 1. announcementRepository.findByIsVisibleTrueAndStatusActive() 로 공고 전체 조회
    // 2. requires_sub_account == true 면 condition.getSubscriptionStatus() 체크
    // 3. income_condition vs condition.getIncomeStatus() 비교
    // 4. target_type vs condition.getAgeStatus() / condition.getFamilyStatus() 비교
    // 5. 필터링된 공고 리스트 반환
    }
}
