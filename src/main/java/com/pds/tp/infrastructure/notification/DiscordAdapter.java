package com.pds.tp.infrastructure.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DiscordAdapter implements Notifier {
    @Override
    public void sendNotification(String channel, String message) {
        // Here we would normally use WebClient or RestTemplate to POST to a Discord Webhook URL
        String jsonPayload = String.format("{\"content\": \"%s\"}", message);
        log.info("[Discord API] Ejecutando HTTP POST al webhook del canal {}: {}", channel, jsonPayload);
    }
}

