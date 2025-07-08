package com.findmypet.service;

import com.findmypet.dto.NotificationEvent;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* Notification-Server에서 알림을 보내는 역할을 담당하는 클래스
 * SseEmitter 객체를 생성하고 관리하며, 이벤트가 수신되면 클라이언트에게 푸시
 * -> 모든 클라이언트와의 지속적인 SSE 연결을 관리하는 중추 클래스
 */
public class NotificationEmitterService {

    // 클라이언트 식별자(userId, 세션ID 등)를 키로,
    // 해당 사용자에게 연결된 SSE 통신 객체(SseEmitter)를 저장
    // 여러 사용자가 동시에 접속하거나 이벤트를 받을 수 있기 때문에, 스레드 안전한 자료구조가 필요
    // Redis에서 이벤트를 수신했을 때, 어떤 클라이언트에게 전송할지를 결정하기 위해 이 맵에서 찾음
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // '/notifications/~~' 같은 API에 접근해 알림을 "구독"할 때 이 메서드가 호출됨
    public SseEmitter createEmitter(String clientId) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30분 타임아웃

        // SSE 연결이 끊어졌거나 타임아웃 되었을 때, 해당 clientId를 맵에서 제거
        // -> 메모리 누수 방지 및 끊어진 클라이언트에게 이벤트 전송되지 않도록 관리
        emitter.onCompletion(() -> emitters.remove(clientId));
        emitter.onTimeout(()    -> emitters.remove(clientId));

        // 새로 생성된 SSE 연결 객체를 Map에 저장 → 향후 푸시 가능하게 함
        emitters.put(clientId, emitter);

        // 생성한 SseEmitter를 컨트롤러로 반환해서 클라이언트에게 연결시킴
        return emitter;
    }

    // RedisNotificationSubscriber에서 이 메서드를 호출
    public void send(NotificationEvent event) {
        // 해당 이벤트의 수신 대상(userId)에 해당하는 클라이언트의 SSE 연결을 찾음
        // 사용자 식별이 세션ID, 토큰의 sub, 클라이언트 고유 ID 등이라면
        // event.getUserId()는 그것과 매핑되어 있어야 함
        SseEmitter emitter = emitters.get(event.getUserId());

        // SSE 프로토콜에 따라 클라이언트에게 단일 이벤트 객체를 보냄
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .id(String.valueOf(event.getTimestamp().toEpochMilli())) // 클라이언트가 끊겼다 재접속할 때 Last-Event-ID로 넘길 수 있는 이벤트 식별자
                        .name(event.getType()) // 이벤트의 종류
                        .data(event)); // 실제 전송할 데이터 → JSON으로 직렬화됨
            } catch (IOException e) {
                emitters.remove(event.getUserId()); // 전송 중 문제가 발생하면, 해당 클라이언트는 더 이상 유효하지 않다고 간주하고 연결을 제거
            }
        }
    }
}
