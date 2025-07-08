package com.findmypet.dto;

import lombok.Data;

import java.time.Instant;

@Data // @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor 포함
public class NotificationEvent {
    private String userId;
    private String type;
    private String message;
    private Instant timestamp;
}

/* API 서버가 Redis에 발행(publish)하는 메시지와,
 * Notification-Server가 SSE로 클라이언트에 전달하는 페이로드
 * JSON 직렬화/역직렬화를 통해,
 * 양쪽 모듈 간 계약(Event Contract)을 명확하게 정의하기 위해 필요한 DTO
 */
