package com.aivle.mini7.controller;

import com.aivle.mini7.client.dto.LoginRequest;
import com.aivle.mini7.client.dto.LoginResponse;
import com.aivle.mini7.client.dto.RegisterRequest;
import com.aivle.mini7.client.dto.RegisterResponse;
import com.aivle.mini7.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // 이게 진짜 유저 컨트롤러

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> signup(@Valid @RequestBody RegisterRequest request) {
        try {
            userService.registerUser(request);
            return ResponseEntity.ok(new RegisterResponse("회원가입 성공"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new RegisterResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new RegisterResponse("회원가입 처리 중 오류가 발생했습니다."));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.loginUser(request.getUserId(), request.getPassword());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new LoginResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new LoginResponse("로그인 처리 중 오류가 발생했습니다.", null));
        }
    }



}