package com.aivle.mini7.service;

import com.aivle.mini7.client.dto.RegisterRequest;
import com.aivle.mini7.entity.User;
import com.aivle.mini7.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(RegisterRequest request) {
        // 중복 검사
        if (userRepository.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException("이미 사용 중인 ID입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 사용자 저장
        User user = new User();
        user.setUserId(request.getUserId());
        user.setPassword(encodedPassword);
        user.setName(request.getName());
        user.setGender(request.getGender());
        user.setAge(request.getAge());

        userRepository.save(user);
    }
}
