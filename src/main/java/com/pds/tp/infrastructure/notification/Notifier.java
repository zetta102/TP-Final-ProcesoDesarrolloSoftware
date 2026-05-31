package com.pds.tp.infrastructure.notification;

public interface Notifier {
    void sendNotification(String channelOrUser, String message);
}

