package com.chcorp.homes.sign.repository;

import com.chcorp.homes.sign.entity.SignRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignRepository extends JpaRepository<SignRequest, Long> {

    List<SignRequest> findByProviderIdOrCustomerIdOrderByUpdatedAtDesc(
            Long providerId,
            Long customerId
    );
}
