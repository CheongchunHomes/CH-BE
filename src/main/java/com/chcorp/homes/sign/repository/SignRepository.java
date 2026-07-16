package com.chcorp.homes.sign.repository;

import com.chcorp.homes.sign.entity.SignRequest;
import com.chcorp.homes.sign.entity.SignStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignRepository extends JpaRepository<SignRequest, Long> {

    List<SignRequest> findByProviderIdOrCustomerIdOrderByUpdatedAtDesc(
            Long providerId,
            Long customerId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update SignRequest s
            set s.status = :nextStatus
            where s.id = :signId
              and s.status = :currentStatus
            """)
    int updateStatusIfCurrent(
            @Param("signId") Long signId,
            @Param("currentStatus") SignStatus currentStatus,
            @Param("nextStatus") SignStatus nextStatus
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update SignRequest s
            set s.status = com.chcorp.homes.sign.entity.SignStatus.CANCELED
            where s.id = :signId
              and s.status in (com.chcorp.homes.sign.entity.SignStatus.ISSUED, com.chcorp.homes.sign.entity.SignStatus.PROVIDER_SIGNED)
            """)
    int cancelIfProcessable(@Param("signId") Long signId);
}
