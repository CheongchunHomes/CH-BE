package com.chcorp.homes.subscription.repository;

import com.chcorp.homes.subscription.entity.SubscriptionHouseType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionHouseTypeRepository extends JpaRepository<SubscriptionHouseType, Long> {

    List<SubscriptionHouseType> findByAnnouncementIdOrderByHouseTypeNameAsc(Long announcementId);

    boolean existsByPblancNoAndModelNoAndHouseTypeName(
            String pblancNo,
            String modelNo,
            String houseTypeName
    );

    // 특별공급 API 호출에 필요한 공고번호와 주택관리번호 조합을 조회한다.
    List<SubscriptionHouseType> findByHouseManageNoIsNotNullAndPblancNoIsNotNull();


}