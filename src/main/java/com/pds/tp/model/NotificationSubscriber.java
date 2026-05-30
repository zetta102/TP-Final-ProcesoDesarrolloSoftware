package com.pds.tp.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationSubscriber {
    private final NotifierFactory notifierFactory;

    public NotificationSubscriber(NotifierFactory notifierFactory) {
        this.notifierFactory = notifierFactory;
    }

    @EventListener
    public void onScrimStateChanged(ScrimStateChangedEvent event) {
        log.info("Event Received: Lobby {} changed to {}", event.getLobbyId(), event.getNuevoEstado());

        Notifier discordNotifier = notifierFactory.createDiscordNotifier();
        discordNotifier.sendNotification("General Channel", "Lobby " + event.getLobbyId() + " is now " + event.getNuevoEstado());
    }
}