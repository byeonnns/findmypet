package com.findmypet.domain.notification;

import com.findmypet.dto.notification.NotificationEvent;

public interface NotificationSubscriber {
    void handleMessage(NotificationEvent event);
}
