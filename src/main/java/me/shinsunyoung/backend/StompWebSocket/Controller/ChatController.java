package me.shinsunyoung.backend.StompWebSocket.Controller;

import me.shinsunyoung.backend.PureWebsocket.DTO.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/chat.sendMessage") // 클라이언트 -> 서버
    @SendTo("/topic/public") // 서버 -> 클라이언트
    public ChatMessage sendMessage(ChatMessage message) {
        return message;
    }
}
