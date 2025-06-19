package me.shinsunyoung.backend.StompWebSocket.Redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisPublisher {

    /* 메시지를 발행하는 클래스 */
    private final StringRedisTemplate stringRedisTemplate;

    // Stomp -> pub -> sub -> stomp
    public void publish(String channel, String message) {

        stringRedisTemplate.convertAndSend(channel, message);
    }


}
