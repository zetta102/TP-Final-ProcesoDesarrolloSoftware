package com.pds.tp.model;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class ProdNotifierFactory implements NotifierFactory {

    private final DiscordAdapter discordAdapter;
    private final SendGridAdapter sendGridAdapter;

    public ProdNotifierFactory(DiscordAdapter discordAdapter, SendGridAdapter sendGridAdapter) {
        this.discordAdapter = discordAdapter;
        this.sendGridAdapter = sendGridAdapter;
    }

    @Override
    public Notifier createPushNotifier() {
        return (user, message) -> System.out.println("[Firebase Push Adapter Stub] " + user + " -> " + message);
    }

    @Override
    public Notifier createEmailNotifier() {
        return sendGridAdapter;
    }

    @Override
    public Notifier createDiscordNotifier() {
        return discordAdapter;
    }
}