package com.pds.tp.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProdNotifierFactory implements NotifierFactory {

    @Override
    public Notifier createPushNotifier() {
        return (user, message) -> log.info("[PUSH to {}] Payload: {}", user, message);
    }

    @Override
    public Notifier createEmailNotifier() {
        return (user, message) -> log.info("[EMAIL to {}] Payload: {}", user, message);
    }

    @Override
    public Notifier createDiscordNotifier() {
        return (channel, message) -> log.info("[DISCORD WEBHOOK: {}] Payload: {}", channel, message);
    }
}