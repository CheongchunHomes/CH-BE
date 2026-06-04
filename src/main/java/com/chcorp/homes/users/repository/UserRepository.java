package com.chcorp.homes.users.repository;

import com.chcorp.homes.users.entity.User;
import com.chcorp.homes.users.entity.UserRole;
import com.chcorp.homes.users.entity.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    @Query("""
            select u
            from User u
            where u.role <> com.chcorp.homes.users.entity.UserRole.ADMIN
              and (:status is null or u.status = :status)
            """)
    Page<User> findNonAdminUsers(
            @Param("status") UserStatus status,
            Pageable pageable
    );

    @Query("""
            select u
            from User u
            where u.role <> com.chcorp.homes.users.entity.UserRole.ADMIN
              and (
                    lower(u.email) like :keyword
                    or lower(u.nickname) like :keyword
              )
              and (:status is null or u.status = :status)
            """)
    Page<User> searchNonAdminUsers(
            @Param("keyword") String keyword,
            @Param("status") UserStatus status,
            Pageable pageable
    );

    List<User> findAllByRoleOrderByIdDesc(UserRole role);

    long countByRole(UserRole role);

    long countByRoleNotAndStatus(UserRole role, UserStatus status);

}
