package com.findmypet.controller;

import com.findmypet.service.NotificationEmitterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/* 클라이언트가 SSE 스트림을 열기 위한 엔드포인트 제공
 * Last-Event-ID 헤더를 통해 끊긴 지점 재전송 로직 구현 가능
 * X-Client-Id(또는 JWT, 세션)로 클라이언트 식별
 */
@RestController
public class NotificationController {
    private final NotificationEmitterService emitterService;

    public NotificationController(NotificationEmitterService emitterService) {
        this.emitterService = emitterService;
    }

    /* X-Client-Id -> 클라이언트의 고유 식별자(예: userId, 세션ID)를 헤더에서 받음
     * Last-Event-ID -> 클라이언트가 중간에 끊겼다가 재연결 시, 마지막으로 받은 이벤트 ID를 보내줌
     *
     */
    @GetMapping("/notifications/stream")
    public SseEmitter streamNotifications(
            @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId,
            @RequestHeader("X-Client-Id") String clientId
    ) {
        // 해당 클라이언트 ID로 SSE 연결 객체를 생성하고 관리용 Map에 등록
        SseEmitter emitter = emitterService.createEmitter(clientId);

        // 이 SseEmitter가 브라우저와 지속적으로 연결되며, 서버는 이후 여기에 알림을 계속 푸시
        return emitter;
    }
}
