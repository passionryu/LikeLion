package me.shinsunyoung.backend.StompWebSocket.Redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.shinsunyoung.backend.StompWebSocket.DTO.ChatMessage;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    /* WebSocket 클라이언트에게 메시지를 전송하거나 브로드캐스트 */
    private final SimpMessagingTemplate simpMessagingTemplate;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {

        try {
            String msgBody = new String(message.getBody());
            ChatMessage chatMessage = objectMapper.readValue(msgBody, ChatMessage.class);

            if (chatMessage.getTo() != null && !chatMessage.getTo().isEmpty()) {
                // 귓속말
                simpMessagingTemplate.convertAndSendToUser(chatMessage.getTo(), "/queue/private", chatMessage);
            } else {
                // 일반 메시지
                simpMessagingTemplate.convertAndSend("/topic/room." + chatMessage.getRoomId(), chatMessage);
            }
        }
        catch (Exception e) {

        }

    }
}
