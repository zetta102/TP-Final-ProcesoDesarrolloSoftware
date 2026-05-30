package com.pds.tp.model;

public interface Notifier {
    void sendNotification(String channelOrUser, String message);
}