package com.code808.calmdesk.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // sockJS를 사용하지 않고 native webSocket 사용
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*"); // TODO: 모든 Origin 허용 (CORS)
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트에서 보낸 메세지를 받을 prefix (C->S)
        registry.setApplicationDestinationPrefixes("/pub");
        // 해당 주소를 구독하고 있는 클라이언트들에게 메세지 전달 (S->C)
        registry.enableSimpleBroker("/sub");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // connect / disconnect 인터셉터
        registration.interceptors(stompHandler);
    }
}
