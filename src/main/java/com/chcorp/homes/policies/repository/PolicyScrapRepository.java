package com.chcorp.homes.policies.repository;

import com.chcorp.homes.policies.entity.PolicyScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyScrapRepository extends JpaRepository<PolicyScrap, Long> {

    // 특정 사용자가 특정 제도를 이미 스크랩 했는지 확인
    boolean existsByUserIdAndPolicyPolicyId(Long userId, Long policyId);

    // 특정 사용자가 특정 제도를 스크랩한 데이터 조회
    // 스크랩 취소할 때 사용
    Optional<PolicyScrap> findByUserIdAndPolicyPolicyId(Long userId, Long policyId);

    // 로그인한 사용자의 제도 스크랩 목록 조회
    // 마이페이지에서 사용
    List<PolicyScrap> findAllByUserId(Long userId);
}
