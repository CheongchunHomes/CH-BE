package com.chcorp.homes.users.service;

import com.chcorp.homes.users.dto.request.LoginDTO;
import com.chcorp.homes.users.dto.request.RegisterDTO;
import com.chcorp.homes.users.dto.response.MyProfileDTO;
import com.chcorp.homes.users.entity.User;
import com.chcorp.homes.users.entity.UserRole;
import com.chcorp.homes.users.entity.UserStatus;
import com.chcorp.homes.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public String login(LoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.email()).orElse(null);
        if(user != null || user.getPassword().equals(loginDTO.password())){
            return user.getNickname();
        }
        return null;
    }

    public String register(RegisterDTO registerDTO) {
        User user = User.builder()
                .email(registerDTO.email())
                .password(registerDTO.password())
                .nickname("default nickname")
                .status(UserStatus.enabled)
                .role(UserRole.USER)
                .build();
        user = userRepository.save(user);
        return user.getNickname();
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
