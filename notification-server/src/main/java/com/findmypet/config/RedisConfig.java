package com.findmypet.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.findmypet.domain.notification.NotificationSubscriber;
import com.findmypet.dto.notification.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/* Notification-Server가 Redis Pub/Sub을 통해 알림 이벤트를 "구독"하고,
 * 수신된 메시지를 내부 로직으로 연결해주는 설정 클래스
 *
 * Redis 채널 구독을 위한 RedisMessageListenerContainer 빈 등록
 * 메시지를 받을 때 호출될 RedisNotificationSubscriber.handleMessage() 연결
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "notification.subscriber",
        havingValue = "redis", matchIfMissing = true)
public class RedisConfig {
    @Value("${notification.channel}")
    private String channel;

    /**
     * Jackson2JsonRedisSerializer 생성자에 ObjectMapper와 타입을 함께 넘겨서,
     * setObjectMapper 호출을 완전히 제거합니다.
     */
    @Bean
    public Jackson2JsonRedisSerializer<NotificationEvent> notificationEventSerializer(ObjectMapper objectMapper) {
        return new Jackson2JsonRedisSerializer<>(objectMapper, NotificationEvent.class);
    }

    /**
     * MessageListenerAdapter를 NotificationSubscriber 인터페이스로 래핑
     */
    @Bean
    public MessageListenerAdapter listenerAdapter(NotificationSubscriber subscriber, Jackson2JsonRedisSerializer<NotificationEvent> serializer) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(subscriber, "handleMessage");
        adapter.setSerializer(serializer);
        return adapter;
    }

    /**
     * RedisMessageListenerContainer에 에러핸들러와 Topic 등록
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory factory, MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(listenerAdapter, new PatternTopic(channel));

        // 에러가 발생해도 컨테이너가 멈추지 않도록 로깅만 수행
        container.setErrorHandler(e -> {
            log.error("[RedisListenerError] " + e.getMessage());
        });
        return container;
    }
}
