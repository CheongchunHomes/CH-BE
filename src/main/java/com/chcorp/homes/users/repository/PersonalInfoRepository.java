package com.chcorp.homes.users.repository;

import com.chcorp.homes.users.entity.PersonalInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalInfoRepository extends JpaRepository<PersonalInfo, Long> {

    boolean existsByUserId(Long userId);

    Optional<PersonalInfo> findByUserId(Long userId);
}
