package com.chcorp.homes.auth.repository;

import com.chcorp.homes.auth.entity.RefreshToken;
import com.chcorp.homes.users.entity.User;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
            delete from RefreshToken token
            where token.user = :user
              and token.userAgent = :userAgent
              and token.expiresAt <= :now
            """)
    void deleteExpiredSessions(
            @Param("user") User user,
            @Param("userAgent") String userAgent,
            @Param("now") Instant now
    );

    @Query("""
            select case when count(token) > 0 then true else false end
            from RefreshToken token
            where token.user = :user
              and token.userAgent = :userAgent
              and token.expiresAt > :now
            """)
    boolean existsActiveSession(
            @Param("user") User user,
            @Param("userAgent") String userAgent,
            @Param("now") Instant now
    );
}
