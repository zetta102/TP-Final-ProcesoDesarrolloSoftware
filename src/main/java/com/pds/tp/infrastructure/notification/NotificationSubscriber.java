package com.pds.tp.infrastructure.notification;

import com.pds.tp.domain.event.ScrimStateChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationSubscriber {
    private static final int MAX_ATTEMPTS = 3;
    private static final long INITIAL_BACKOFF_MS = 200L;

    private final NotifierFactory notifierFactory;

    public NotificationSubscriber(NotifierFactory notifierFactory) {
        this.notifierFactory = notifierFactory;
    }

    @EventListener
    public void onDomainEvent(ScrimStateChangedEvent event) {
        Notifier discord = notifierFactory.createDiscordNotifier();
        Notifier email = notifierFactory.createEmailNotifier();
        Notifier push = notifierFactory.createPushNotifier();

        String message = String.format("El Lobby %s ha cambiado de estado a: %s",
                event.getLobbyId(), event.getNuevoEstado());

        // Retry with exponential backoff to tolerate transient provider errors.
        sendWithRetry(discord, "#scrim-updates", message, "DISCORD");
        sendWithRetry(email, "all-players@scrims.local", message, "EMAIL");
        sendWithRetry(push, "all-players", message, "PUSH");
    }

    private void sendWithRetry(Notifier notifier, String target, String message, String channel) {
        long backoff = INITIAL_BACKOFF_MS;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                notifier.sendNotification(target, message);
                return;
            } catch (RuntimeException ex) {
                if (attempt == MAX_ATTEMPTS) {
                    log.error("No se pudo enviar notificación por {} tras {} intentos", channel, MAX_ATTEMPTS, ex);
                    return;
                }

                log.warn("Fallo al enviar por {} (intento {}), reintentando...", channel, attempt, ex);
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    log.error("Reintento interrumpido para canal {}", channel, interruptedException);
                    return;
                }
                backoff *= 2;
            }
        }
    }
}

