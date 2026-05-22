package com.chcorp.homes.subscription.repository;

import com.chcorp.homes.subscription.entity.SubscriptionSpecialSupplyStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionSpecialSupplyStatRepository extends JpaRepository<SubscriptionSpecialSupplyStat, Long> {

    // 같은 공고번호와 주택관리번호의 기존 특별공급 현황을 삭제한다.
    void deleteByPblancNoAndHouseManageNo(String pblancNo, String houseManageNo);

    // 공고에 특별공급 현황 데이터가 있는지 확인한다.
    boolean existsByAnnouncementId(Long announcementId);
}