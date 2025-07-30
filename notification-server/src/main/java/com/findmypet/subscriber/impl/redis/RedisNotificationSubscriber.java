package com.findmypet.subscriber.impl.redis;

import com.findmypet.domain.notification.NotificationSubscriber;
import com.findmypet.dto.notification.NotificationEvent;
import com.findmypet.service.NotificationEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "notification.subscriber", havingValue = "redis", matchIfMissing = true)
public class RedisNotificationSubscriber implements NotificationSubscriber {

    private final NotificationEmitterService emitterService;

    @Override
    public void handleMessage(NotificationEvent event) {
        emitterService.send(event);
    }
}
