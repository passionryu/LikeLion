package me.shinsunyoung.backend.StompWebSocket.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.shinsunyoung.backend.StompWebSocket.DTO.ChatMessage;
import me.shinsunyoung.backend.StompWebSocket.Gpt.GPTService;
import me.shinsunyoung.backend.StompWebSocket.Redis.RedisPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    //서버가 클라이언트에게 수동으로 메세지를 보낼 수 있도록 하는 클래스
    private final SimpMessagingTemplate template;
    private final RedisPublisher redisPublisher;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final GPTService gptService;

    @Value("${PROJECT_NAME:web Server}")
    private String instansName;

    //단일 브로드캐스트 (방을 동적으로 생성이 안됨)
    @MessageMapping("/gpt")
    public void sendMessageGPT(ChatMessage message) throws Exception {

        template.convertAndSend("/topic/gpt", message);

        String getResponse = gptService.gptMessage(message.getMessage());

        ChatMessage chatMessage = new ChatMessage("난 GPT", getResponse);

        template.convertAndSend("/topic/gpt", chatMessage);

    }

    @MessageMapping("/chat.sendMessage")
    public void sendmessage(ChatMessage message) throws JsonProcessingException {

        // 채팅 앞에 몇번 서버인지 조회
        message.setMessage(instansName + " " + message.getMessage());

        String channel = null;
        String msg = null;

        if (message.getTo() != null && !message.getTo().isEmpty()) {
            // 귓속말
            channel = "private." + message.getRoomId();
            msg = objectMapper.writeValueAsString(message);
        } else {
            // 일반 메시지
            channel = "room." + message.getRoomId();
            msg = objectMapper.writeValueAsString(message);
        }
        redisPublisher.publish(channel, msg);
    }
}
