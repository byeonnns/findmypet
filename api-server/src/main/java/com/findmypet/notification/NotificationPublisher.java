package com.findmypet.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.findmypet.dto.notification.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationPublisher {

    // 스프링에서 제공하는 문자열 기반 Redis 클라이언트
    // convertAndSend() (CAS)로 문자열 메시지를 Redis 채널에 발행(publish) 할 수 있음
    // RedisTemplate<String, Object> 등도 사용 가능하지만 현재 문자열만 사용하므로 채택
    private final StringRedisTemplate redisTemplate;

    // NotificationEvent를 JSON 문자열로 직렬화하는 데 사용되는 Jackson 라이브러리
    private final ObjectMapper objectMapper;

    @Value("${notification.channel}")
    private String channel;

    public NotificationPublisher(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    // 실제로 알림을 보내는 API -> 컨트롤러나 서비스 레이어에서 호출
    public void publish(NotificationEvent event) {
        try {
            // NotificationEvent는 자바 객체이므로 Redis에 직접 보낼 수 없음 -> JSON 문자열로 반환
            String message = objectMapper.writeValueAsString(event);
            // Redis의 PUBLISH 명령과 동일
            // 이 채널을 구독하고 있는 곳이 message 수신
            redisTemplate.convertAndSend(channel, message);

            log.info("[알림 발행] type = {}, to = {}, message = {}",
                    event.getType(), event.getUserId(), event.getMessage());
            log.info("message={}", event.getMessage());

        } catch (JsonProcessingException e) {
            log.warn("JSON 직렬화 오류 발생");
        }
    }
}
