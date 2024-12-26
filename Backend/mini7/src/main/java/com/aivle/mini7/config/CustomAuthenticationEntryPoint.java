package com.aivle.mini7.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 인증 실패 시 응답을 처리하는 클래스.
     * - 인증되지 않은 요청이 들어오면 JSON 형식으로 에러 메시지 반환.
     */

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String errorMessage = (String) request.getAttribute("authError");

        if (errorMessage == null) {
            errorMessage = "Unauthorized access.";
        }

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + errorMessage + "\"}");
    }
}
