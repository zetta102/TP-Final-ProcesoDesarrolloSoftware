package com.pds.tp.infrastructure.notification;

public interface NotifierFactory {
    Notifier createPushNotifier();

    Notifier createEmailNotifier();

    Notifier createDiscordNotifier();

    Notifier createICalNotifier();
}



