package me.shinsunyoung.backend.PureWebsocket.Config;

import me.shinsunyoung.backend.PureWebsocket.Handler.ChatWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

//@Configuration
//@EnableWebSocket // 웹 소켓사용 시 사용하는 어노테이션
public class WebSocketConfig implements WebSocketConfigurer {

    /**
     * ws 엔트 포인트로 요청을 보낼 수 있는지 결정하는 보안 정책 설정
     * * <- 모든 도메인에서 접근 가능
     *
     * @param registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatWebSocketHandler(), "/ws-chat")
                .setAllowedOriginPatterns("*");
    }
}
