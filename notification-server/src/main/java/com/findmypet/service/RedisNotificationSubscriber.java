package com.findmypet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.findmypet.dto.notification.NotificationEvent;

import java.io.IOException;

/* Redis에서 받은 Raw JSON 문자열을 NotificationEvent로 변환
 * 변환된 이벤트를 SSE 푸시 서비스(NotificationEmitterService)에 전달
 * -> Redis Pub/Sub 채널에서 수신한 메시지를 실시간 SSE로 사용자에게 전달하는 중간 책임
 */
public class RedisNotificationSubscriber {

    // 문자열(JSON) → NotificationEvent 자바 객체로 변환하기 위해 필요
    private final ObjectMapper objectMapper;
    // 변환된 이벤트를 해당 유저에게 SSE로 푸시하는 핵심 서비스
    private final NotificationEmitterService emitterService;

    public RedisNotificationSubscriber(ObjectMapper objectMapper,
                                       NotificationEmitterService emitterService) {
        this.objectMapper = objectMapper;
        this.emitterService = emitterService;
    }

    // Redis Pub/Sub을 통해 수신한 Raw JSON 문자열을 파라미터로 받음
    public void handleMessage(String messageJson) {
        try {
            NotificationEvent event = objectMapper.readValue(messageJson, NotificationEvent.class);
            // 변환된 이벤트를 해당 사용자에게 전송하기 위해 NotificationEmitterService의 send() 메서드를 호출
            // 이 메서드는 내부적으로 SseEmitter를 찾아 emitter.send(...)를 수행함 → 클라이언트 브라우저로 이벤트가 도착
            // 사용자 단위로 필터링할 수 있음 (userId 기준)
            emitterService.send(event);
        } catch (IOException e) {
            // 로깅 및 예외 처리
            e.printStackTrace();
        }
    }
}
