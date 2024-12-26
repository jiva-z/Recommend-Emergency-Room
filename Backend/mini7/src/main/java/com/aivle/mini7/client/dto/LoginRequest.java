package com.aivle.mini7.client.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "ID를 입력해 주세요.")
    private String userId;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String password;

    // Getters and Setters
    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getPassword() { return password; }

    public void setPassword(String password) {this.password = password; }
}
