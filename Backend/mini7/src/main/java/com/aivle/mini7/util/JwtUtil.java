package com.aivle.mini7.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey; // application.properties 또는 환경 변수에서 가져옴

    @Value("${jwt.expiration_time}")
    private long expirationTime; // 토큰 만료 시간

    // JWT 생성 (userId 기반)
    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId) // userId를 subject로 사용
                .setIssuedAt(new Date()) // 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // 만료 시간
                .signWith(SignatureAlgorithm.HS256, secretKey) // 서명
                .compact();
    }

    // JWT 검증 및 클레임 추출
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰에서 userId 추출
    public String extractUserId(String token) {
        return extractClaims(token).getSubject(); // subject에 저장된 userId를 반환
    }

    // 토큰 만료 여부 확인
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}
