package com.aivle.mini7.config;

import com.aivle.mini7.filter.JwtAuthenticationFilter;
import com.aivle.mini7.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    // JwtUtil 의존성 주입
    public SecurityConfig(JwtUtil jwtUtil, CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.jwtUtil = jwtUtil;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    /**
     * 비밀번호 암호화를 위한 BCryptPasswordEncoder 빈 등록
     * Spring Security에서 사용자 비밀번호를 안전하게 저장하도록 지원.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Security의 필터 체인 설정.
     * - CSRF 비활성화: REST API에서는 CSRF가 필요하지 않음.
     * - Stateless 세션 관리: JWT 기반 인증에서는 서버에서 세션을 유지하지 않음.
     * - 요청 인증 및 권한 설정: /auth/login 및 /auth/register는 인증 없이 접근 가능, 나머지는 인증 필요.
     * - JWT 인증 필터 추가: 요청이 컨트롤러에 도달하기 전에 JWT를 검증.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 추가
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 관리 비활성화
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/auth/register",  "/", "/login", "/images/**", "/css/**", "/js/**").permitAll() // 인증 없이 접근 가능한 엔드포인트
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(new CustomAuthenticationEntryPoint()) // 인증 실패 처리
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가

        return http.build();
    }

    /**
     * AuthenticationManager를 빈으로 등록.
     * 인증 프로세스에서 사용되며, 로그인 처리에 필요.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * CORS 설정.
     * - 모든 Origin, HTTP 메서드, 헤더 허용 (운영 환경에서는 적절히 제한 필요)
     * - 인증 정보를 포함한 요청 허용.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*"); // 모든 Origin 허용 (운영 환경에서는 제한 필요)
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 인증 정보 허용 (JWT 등)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
