package com.findmypet.domain.notification;

import com.findmypet.dto.notification.NotificationEvent;

/**
 * 알림 이벤트를 외부에 발행하는 메서드를 추상화한 인터페이스
 * 구현체에 따라 Redis Pub/Sub, Kafka, HTTP 등을 사용할 수 있다.
 */
public interface NotificationPublisher {
    void publish(NotificationEvent event);
}
