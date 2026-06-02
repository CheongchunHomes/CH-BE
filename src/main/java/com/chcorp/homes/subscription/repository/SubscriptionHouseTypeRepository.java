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

}
