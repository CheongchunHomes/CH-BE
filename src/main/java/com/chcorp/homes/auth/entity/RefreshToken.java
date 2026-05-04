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

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(name = "ip_address")
    private String ipAddress;

    public boolean isExpired(Instant now) {
        return !expiresAt.isAfter(now);
    }
}
