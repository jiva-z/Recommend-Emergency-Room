package com.aivle.mini7.controller;

import com.aivle.mini7.entity.User;
import com.aivle.mini7.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hospital/users")
public class UserController {

    // DB 사용자 조회 및 생성 테스트를 위한 UserController.

    @Autowired
    private UserRepository userRepository;

    // 모든 사용자 조회 (보호되지 않은 엔드포인트 - 인증 불필요)
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 사용자 생성 (보호되지 않은 엔드포인트 - 인증 불필요)
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    // 보호된 엔드포인트 - JWT 인증 필요
    @GetMapping("/protected")
    public String protectedEndpoint() {
        return "이 데이터는 인증된 사용자만 볼 수 있습니다.";
    }
}
