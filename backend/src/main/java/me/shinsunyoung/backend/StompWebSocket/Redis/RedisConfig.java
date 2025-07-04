package me.shinsunyoung.backend.StompWebSocket.Redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Profile("!test")
@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisSubscriber redisSubscriber;

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;


    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();

        //도커로 할떈 아래 반드시 해야 함
        configuration.setHostName(host);
        configuration.setPort(port);

        return new LettuceConnectionFactory(configuration); // localhost:6379로 기본 연결
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer   Container = new RedisMessageListenerContainer();
        Container.setConnectionFactory(redisConnectionFactory);

        Container.addMessageListener(new MessageListenerAdapter(redisSubscriber), new PatternTopic("room.*"));
        Container.addMessageListener(new MessageListenerAdapter(redisSubscriber), new PatternTopic("private.*")); //귓속말

        return Container;
    }

}
