package com.chcorp.homes.loans.repository;

import com.chcorp.homes.loans.entity.LoanApplication;
import com.chcorp.homes.loans.entity.LoanApplicationStatus;
import com.chcorp.homes.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    List<LoanApplication> findAllByUser(User user);

    List<LoanApplication> findAllByStatus(LoanApplicationStatus status);

    Optional<LoanApplication> findFirstByUserOrderByUpdatedAtDescApplicationIdDesc(User user);

    @Query("""
            select a
            from LoanApplication a
            join fetch a.user u
            where a.applicationId = :applicationId
            """)
    Optional<LoanApplication> findByApplicationIdWithUser(Long applicationId);

    @Query("""
            select a
            from LoanApplication a
            join fetch a.user u
            join fetch a.loanProduct p
            order by a.applicationId desc
            """)
    List<LoanApplication> findAllWithUserAndLoanProduct();
}
