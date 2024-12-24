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

    // 모든 사용자 조회
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 사용자 생성
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }
}
