package com.pds.tp.infrastructure.notification;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class NotifierFactoryTest {

    @Test
    void prodFactoryShouldCreateAllNotifiers() {
        ProdNotifierFactory factory = new ProdNotifierFactory(new DiscordAdapter(), new SendGridAdapter());

        assertNotNull(factory.createDiscordNotifier());
        assertNotNull(factory.createEmailNotifier());
        assertNotNull(factory.createPushNotifier());
    }
}

