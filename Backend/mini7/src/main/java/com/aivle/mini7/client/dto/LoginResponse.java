package com.aivle.mini7.client.dto;

public class LoginResponse {

    private String message;
    private String token;

    public LoginResponse(String message, String token) {
        this.message = message;
        this.token = token;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
}
