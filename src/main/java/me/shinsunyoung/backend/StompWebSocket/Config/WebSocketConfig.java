package me.shinsunyoung.backend.StompWebSocket.Config;

import me.shinsunyoung.backend.StompWebSocket.Handler.CustomHandShakeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic","/queue"); // 구독용 경로 서버 -> 클라이언트
        registry.setApplicationDestinationPrefixes("/app"); // 전송용 경로 클라이언트 -> 서버

        // user 특정 사용자에게 메세지를 보낼 접두어
        /** 서버가 특정 사용자에게 메시지를 보낼 때, 클라이언트가 구독할 경로 접두어 **/
        registry.setUserDestinationPrefix("/user"); // 서버 -> 특정 사용자
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        /* 단체 채팅방 용 엔트 포인트 */
        registry.addEndpoint("/ws-chat")
                .setHandshakeHandler(new CustomHandShakeHandler())
                .setAllowedOrigins("*");

        /* GPT용 엔트 포인트 */
        registry.addEndpoint("/ws-gpt")
                .setAllowedOrigins("*");
    }


}
