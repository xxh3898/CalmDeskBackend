package com.code808.calmdesk.global.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.code808.calmdesk.global.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // CONNECT 명령일 때만 토큰 검증
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authorizationHeader = accessor.getFirstNativeHeader("Authorization");

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                log.error("헤더가 없거나 Bearer 타입이 아님");
                throw new AccessDeniedException("토큰이 유효하지 않습니다.");
            }

            String token = authorizationHeader.substring(7);

            // 토큰 유효성 검증
            if (jwtTokenProvider.validateToken(token).isEmpty()) {
                log.error("유효하지 않은 JWT 토큰");
                throw new AccessDeniedException("토큰이 유효하지 않습니다.");
            }

            log.info("STOMP 연결 승인: {}", token);
        }
        return message;
    }
}
