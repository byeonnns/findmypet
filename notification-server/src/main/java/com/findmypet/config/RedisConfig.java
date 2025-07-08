package com.findmypet.config;

import com.findmypet.service.RedisNotificationSubscriber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/* Notification-Server가 Redis Pub/Sub을 통해 알림 이벤트를 "구독"하고,
 * 수신된 메시지를 내부 로직으로 연결해주는 설정 클래스
 *
 * Redis 채널 구독을 위한 RedisMessageListenerContainer 빈 등록
 * 메시지를 받을 때 호출될 RedisNotificationSubscriber.handleMessage() 연결
 */
@Configuration
public class RedisConfig {
    @Value("${notification.channel}")
    private String channel;

    /*
    파라미터 설명
    RedisConnectionFactory factory: 스프링이 자동으로 관리하는 Redis 연결 객체
    MessageListenerAdapter listenerAdapter: 뒤에서 등록할 실제 메시지 핸들러
     */
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory factory,
                                            MessageListenerAdapter listenerAdapter) {
        // RedisMessageListenerContainer를 새로 생성
        // Redis 서버와의 연결을 관리하는 factory 주입
        // 이 객체가 백그라운드 쓰레드로 실행되며, Redis 채널을 지속적으로 감시
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);

        // listenerAdapter가 구독 채널의 메시지를 받을 리스너로 등록
        // PatternTopic(channel)
        //      -> 단일 채널 구독: "findmypet-notifications"
        //      -> 와일드카드 구독도 가능: "findmypet-*" → 모든 findmypet-로 시작하는 채널 구독
        // 해당 Redis 채널에 누군가 PUBLISH 명령으로 메시지를 보내면, Notification-Server가 자동으로 수신
        container.addMessageListener(listenerAdapter, new PatternTopic(channel));
        return container;
    }

    /* 파라미터 설명
     * MessageListenerAdapter: Redis의 Raw 메시지를 자바 객체로 쉽게 매핑해주는 어댑터 패턴 구현체
     * 인자로 넘기는 subscriber -> RedisNotificationSubscriber 인스턴스 주입
     */
    @Bean
    MessageListenerAdapter listenerAdapter(RedisNotificationSubscriber subscriber) {
        // Redis Pub/Sub 메시지가 수신되면 subscriber.handleMessage 메서드를 호출
        // 메시지는 JSON 문자열로 넘어오며, 해당 메서드 내에서
        //JSON → NotificationEvent로 파싱
        //클라이언트 SSE로 푸시 등 후속 작업 진행
        return new MessageListenerAdapter(subscriber, "handleMessage");
    }
}
