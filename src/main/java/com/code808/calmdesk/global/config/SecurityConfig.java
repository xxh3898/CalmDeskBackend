package com.code808.calmdesk.global.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.code808.calmdesk.global.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/webjars/**"
                ).permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/subscribe/**").permitAll()
                .requestMatchers("/api/notifications/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/business-card/extract").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/companies/by-code/{company_code}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/companies/genderate-code").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/companies/register").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/companies/join").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/emplpoyee/attendance/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/attendance/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/employee/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/employee/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/consultations/count").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/departments/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/consultations/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/admin/shop/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/admin/shop/items/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/dashboard/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/employee/dashboard/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/admin/shop/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/admin/shop/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/employee/shop/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/departments/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/mypage/**").authenticated()
                .requestMatchers("/api/chat", "/api/chat/**").permitAll()
                .requestMatchers("/api/admin/mypage/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/admin/team/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/admin/team/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/chat/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/chat/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/chat/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/chat/**").authenticated()
                .requestMatchers("/ws-stomp/**").permitAll()
                .requestMatchers("/api/business-card/**").authenticated()
                .requestMatchers("/api/call-records/**").authenticated()
                .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "https://calmdesk.cloud",
                "https://www.calmdesk.cloud"
        ));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
