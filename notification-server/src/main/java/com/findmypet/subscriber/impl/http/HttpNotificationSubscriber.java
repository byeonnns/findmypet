package com.findmypet.subscriber.impl.http;

import com.findmypet.domain.notification.NotificationSubscriber;
import com.findmypet.dto.notification.NotificationEvent;
import com.findmypet.service.NotificationEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ConditionalOnProperty(name = "notification.subscriber", havingValue = "http")
@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
public class HttpNotificationSubscriber implements NotificationSubscriber {

    // 변환된 이벤트를 해당 유저에게 SSE로 푸시하는 핵심 서비스
    private final NotificationEmitterService emitterService;

    @PostMapping
    public ResponseEntity<Void> handleNotification(@RequestBody NotificationEvent event) {
        handleMessage(event);
        return ResponseEntity.ok().build();
    }

    @Override
    public void handleMessage(NotificationEvent event) {
        emitterService.send(event);
    }
}
