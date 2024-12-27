package com.aivle.mini7.filter;

import com.aivle.mini7.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;


public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // JwtUtil을 주입받아 JWT 검증에 사용
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 요청 헤더에서 Authorization 값 추출
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // "Bearer " 이후의 토큰 값만 추출
            try {
                if (!jwtUtil.isTokenExpired(token)) {
                    String userId = jwtUtil.extractUserId(token);
                    // SecurityContext에 인증 정보 설정
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(userId, null, null)
                    );
                } else {
                    request.setAttribute("authError", "Token has expired.");
                }
            } catch (SignatureException e) {
                request.setAttribute("authError", "Invalid token signature.");
            } catch (MalformedJwtException e) {
                request.setAttribute("authError", "Malformed token.");
            } catch (ExpiredJwtException e) {
                request.setAttribute("authError", "Token has expired.");
            } catch (Exception e) {
                request.setAttribute("authError", "Invalid token.");
            }
        } else {
            request.setAttribute("authError", "Missing Authorization header.");
        }

        filterChain.doFilter(request, response);
    }
}
