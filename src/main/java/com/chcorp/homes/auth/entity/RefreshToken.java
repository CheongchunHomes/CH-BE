package com.chcorp.homes.auth.entity;

import com.chcorp.homes.common.entity.MutableBaseEntity;
import com.chcorp.homes.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends MutableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "reauthed_at")
    private Instant reauthedAt;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    public boolean isExpired(Instant now) {
        return !expiresAt.isAfter(now);
    }

    /**
     * 재인증 성공 시 호출한다.
     * 기존 row를 삭제하지 않고 reauthedAt과 expiresAt을 갱신한다.
     */
    public void updateForReauth(Instant reauthedAt, Instant newExpiresAt) {
        this.reauthedAt = reauthedAt;
        this.expiresAt = newExpiresAt;
    }
}
