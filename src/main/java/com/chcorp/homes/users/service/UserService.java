package com.chcorp.homes.users.service;

import com.chcorp.homes.users.dto.request.RegisterDTO;
import com.chcorp.homes.users.dto.response.MyProfileDTO;
import com.chcorp.homes.users.entity.User;
import com.chcorp.homes.users.entity.UserRole;
import com.chcorp.homes.users.entity.UserStatus;
import com.chcorp.homes.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(RegisterDTO registerDTO) {
        validateRegisterDTO(registerDTO);

        if (userRepository.findByEmail(registerDTO.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        User user = User.builder()
                .email(registerDTO.email())
                .password(passwordEncoder.encode(registerDTO.password()))
                .nickname(registerDTO.nickname())
                .status(UserStatus.enabled)
                .role(UserRole.USER)
                .build();

        userRepository.save(user);
    }

    private void validateRegisterDTO(RegisterDTO registerDTO) {
        if (registerDTO == null || isBlank(registerDTO.email()) || isBlank(registerDTO.password()) || isBlank(registerDTO.nickname())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email, password, and nickname are required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public MyProfileDTO mypage(String nickname) {
        User user = userRepository.findByNickname(nickname).orElse(null);
        MyProfileDTO myProfileDTO = new MyProfileDTO(
                user.getEmail(),
                user.getNickname()
        );
        return myProfileDTO;
    }
}
