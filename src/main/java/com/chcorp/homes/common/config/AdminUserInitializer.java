package com.chcorp.homes.common.config;

import com.chcorp.homes.users.entity.UserRole;
import com.chcorp.homes.users.entity.UserStatus;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 1번과 2번 유저 고정시키기
 * */
@RequiredArgsConstructor
@Component
public class AdminUserInitializer implements ApplicationRunner {

    private static final String DEFAULT_PASSWORD = "admin";

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        assertReservedValuesAreAvailable();
        releaseReservedValuesFromTargetRows();
        upsertAdminUser(1L, "admin@admin", "ADMIN");
        upsertAdminUser(2L, "lh@admin", "주택공사");
        resetUserIdSequence();
    }

    private void assertReservedValuesAreAvailable() {
        List<Long> conflictIds = jdbcTemplate.queryForList(
                """
                select id
                from users
                where id not in (?, ?)
                  and (email in (?, ?) or nickname in (?, ?))
                limit 1
                """,
                Long.class,
                1L,
                2L,
                "admin@admin",
                "lh@admin",
                "ADMIN",
                "주택공사"
        );

        if (!conflictIds.isEmpty()) {
            throw new IllegalStateException(
                    "Reserved admin email or nickname is already used by user id " + conflictIds.get(0)
            );
        }
    }

    private void releaseReservedValuesFromTargetRows() {
        String token = UUID.randomUUID().toString().replace("-", "");
        jdbcTemplate.update(
                """
                update users
                set email = concat('__admin_seed_', ?, '_', id),
                    nickname = concat('__admin_seed_', ?, '_', id)
                where id in (?, ?)
                """,
                token,
                token,
                1L,
                2L
        );
    }

    private void upsertAdminUser(Long id, String email, String nickname) {
        jdbcTemplate.update(
                """
                insert into users (id, email, password, nickname, status, role, created_at)
                values (?, ?, ?, ?, ?, ?, now())
                on conflict (id) do update
                set email = excluded.email,
                    password = excluded.password,
                    nickname = excluded.nickname,
                    status = excluded.status,
                    role = excluded.role
                """,
                id,
                email,
                passwordEncoder.encode(DEFAULT_PASSWORD),
                nickname,
                UserStatus.enabled.name(),
                UserRole.ADMIN.name()
        );
    }

    private void resetUserIdSequence() {
        jdbcTemplate.execute(
                """
                select setval(
                    pg_get_serial_sequence('users', 'id'),
                    greatest((select coalesce(max(id), 1) from users), 2),
                    true
                )
                """
        );
    }
}
