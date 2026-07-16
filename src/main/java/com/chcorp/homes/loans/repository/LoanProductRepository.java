package com.chcorp.homes.loans.repository;

import com.chcorp.homes.loans.entity.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanProductRepository extends JpaRepository<LoanProduct, Long> {
    Optional<LoanProduct> findByProviderAndExternalCode(String provider, String externalCode);
}
