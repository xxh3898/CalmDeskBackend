package com.code808.calmdesk.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 Origin
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("https://calmdesk.cloud");
        config.addAllowedOrigin("https://www.calmdesk.cloud");

        // 허용할 HTTP 메서드
        config.addAllowedMethod("*");

        // 허용할 헤더
        config.addAllowedHeader("*");

        // Credentials 허용
        config.setAllowCredentials(true);

        // 모든 경로에 적용
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
