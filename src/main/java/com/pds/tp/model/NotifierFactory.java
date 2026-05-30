package com.pds.tp.model;

public interface NotifierFactory {
    Notifier createPushNotifier();
    Notifier createEmailNotifier();
    Notifier createDiscordNotifier();
}

