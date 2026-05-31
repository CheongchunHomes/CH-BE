package com.chcorp.homes.sign.repository;

import com.chcorp.homes.sign.entity.SignContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SignContractRepository extends JpaRepository<SignContract, Long> {

    Optional<SignContract> findBySignRequestId(Long signRequestId);

    boolean existsBySignRequestId(Long signRequestId);
}
