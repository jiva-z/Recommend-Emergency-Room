package com.aivle.mini7;

import com.aivle.mini7.client.dto.RegisterRequest;
import com.aivle.mini7.entity.User;
import com.aivle.mini7.repository.UserRepository;
import com.aivle.mini7.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class SignupApiTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void clearDatabase() {
        entityManager.createNativeQuery("TRUNCATE TABLE user").executeUpdate();
    }

    @Test
    void testDuplicateUserId() {
        // Arrange: 이미 존재하는 사용자 생성
        User existingUser = new User();
        existingUser.setUserId("duplicateId");
        existingUser.setPassword(passwordEncoder.encode("password123"));
        existingUser.setName("테스트 사용자");
        existingUser.setGender("M");
        existingUser.setAge(25);
        userRepository.save(existingUser);

        // Act & Assert: 중복된 ID로 회원가입 시도 -> 예외 발생 확인
        RegisterRequest duplicateRequest = new RegisterRequest();
        duplicateRequest.setUserId("duplicateId"); // 중복된 ID
        duplicateRequest.setPassword("newpassword123");
        duplicateRequest.setName("다른 사용자");
        duplicateRequest.setGender("F");
        duplicateRequest.setAge(30);

        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(duplicateRequest);
        });
    }
}
