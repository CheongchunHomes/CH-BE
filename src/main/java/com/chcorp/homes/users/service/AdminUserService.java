package com.chcorp.homes.users.service;

import com.chcorp.homes.users.entity.User;
import com.chcorp.homes.users.entity.UserRole;
import com.chcorp.homes.users.entity.UserStatus;
import com.chcorp.homes.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<User> getUsers(String keyword, String status, Pageable pageable) {
        String normalizedKeyword = normalize(keyword);
        UserStatus normalizedStatus = parseOptionalStatus(status);

        if (normalizedKeyword == null) {
            return userRepository.findNonAdminUsers(normalizedStatus, pageable);
        }

        return userRepository.searchNonAdminUsers(
                "%" + normalizedKeyword.toLowerCase(Locale.ROOT) + "%",
                normalizedStatus,
                pageable
        );
    }

    @Transactional(readOnly = true)
    public List<User> getAdminUsers() {
        return userRepository.findAllByRoleOrderByIdDesc(UserRole.ADMIN);
    }

    @Transactional
    public void changeUserStatus(Long userId, String status) {
        UserStatus nextStatus = parseStatus(status);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRole() == UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ADMIN users cannot be changed");
        }

        user.setStatus(nextStatus);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public long countRegisteredUsers() {
        return userRepository.count();
    }

    @Transactional(readOnly = true)
    public long countActiveUsers() {
        return userRepository.countByRoleNotAndStatus(UserRole.ADMIN, UserStatus.enabled);
    }

    @Transactional(readOnly = true)
    public long countDisabledUsers() {
        return userRepository.countByRoleNotAndStatus(UserRole.ADMIN, UserStatus.disabled);
    }

    @Transactional(readOnly = true)
    public long countAdminUsers() {
        return userRepository.countByRole(UserRole.ADMIN);
    }

    private UserStatus parseOptionalStatus(String status) {
        String normalized = normalize(status);
        return normalized == null ? null : parseStatus(normalized);
    }

    private UserStatus parseStatus(String status) {
        String normalized = normalize(status);
        if (normalized == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }

        try {
            return UserStatus.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status must be enabled or disabled");
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }
}
