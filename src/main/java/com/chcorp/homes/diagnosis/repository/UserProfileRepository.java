package com.chcorp.homes.diagnosis.repository;

import com.chcorp.homes.diagnosis.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    // user_id로 프로필 1건 조회 (UPSERT 판단용)
    Optional<UserProfile> findByUserId(Long userId);
}