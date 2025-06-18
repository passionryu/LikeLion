package me.shinsunyoung.backend.StompWebSocket.Controller;

import lombok.RequiredArgsConstructor;
import me.shinsunyoung.backend.PureWebsocket.DTO.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    // 단일 브로드 캐스트 (방을 동적으로 생성이 안됨)
//    @MessageMapping("/chat.sendMessage") // 클라이언트 -> 서버
//    @SendTo("/topic/public") // 서버 -> 클라이언트
//    public ChatMessage sendMessage(ChatMessage message) {
//        return message;
//    }

    // 서버가 클라이언트에게 수동으로 메시지를 보낼 수 있게 하는 클래스
    private final SimpMessagingTemplate template;

    // 동적으로 방 생성
    @MessageMapping("/chat.sendMessage")
    public void sendmessage(ChatMessage message){

        // message 에서 roomId를 추출해서 해당 roomId를 구독하고 있는 클라이언트들에게 메시지를 전달
        template.convertAndSend("/topic/"+message.getRoomId(), message);
    }

}
