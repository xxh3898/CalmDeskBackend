package com.code808.calmdesk.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // POST/PUT 요청을 위해 CSRF 비활성화
                .cors(Customizer.withDefaults()) // CORS 설정 적용
                .authorizeHttpRequests(auth -> auth
                        // ⭐ 이 줄이 핵심입니다. 해당 경로를 인증 없이 허용합니다.
                        .requestMatchers("/api/shop/**").permitAll()
                        .anyRequest().authenticated()
                )
                // 기본 로그인 폼이 뜨지 않게 하거나 설정을 조정해야 합니다.
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}