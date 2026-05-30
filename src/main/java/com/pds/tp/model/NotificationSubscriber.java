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
    public void onDomainEvent(ScrimStateChangedEvent event) {
        Notifier discord = notifierFactory.createDiscordNotifier();
        Notifier email = notifierFactory.createEmailNotifier();

        String message = String.format("El Lobby %s ha cambiado de estado a: %s",
                event.getLobbyId(), event.getNuevoEstado());

        // Dispatch notifications via our Abstract Factory created handlers
        discord.sendNotification("#scrim-updates", message);
        email.sendNotification("all-players@scrims.local", message);
    }
}