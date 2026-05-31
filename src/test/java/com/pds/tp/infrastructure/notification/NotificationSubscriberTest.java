package com.pds.tp.infrastructure.notification;

import com.pds.tp.domain.entity.Lobby;
import com.pds.tp.domain.entity.Player;
import com.pds.tp.domain.event.ScrimStateChangedEvent;
import com.pds.tp.infrastructure.repository.LobbyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationSubscriberTest {

    @Mock
    private NotifierFactory notifierFactory;

    @Mock
    private LobbyRepository lobbyRepository;

    @Mock
    private Notifier emailNotifier;

    @Mock
    private Notifier pushNotifier;

    @Mock
    private Notifier discordNotifier;

    @Mock
    private Notifier iCalNotifier;

    @Test
    void shouldNotifyEachLobbyMemberWhenAllPlayersConfirmed() {
        Player host = new Player("host", "host@test.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");
        Player guest = new Player("guest", "guest@test.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");

        UUID lobbyId = UUID.randomUUID();
        Lobby lobby = new Lobby(
                LocalDateTime.now().plusHours(1),
                2,
                1,
                "LAS",
                "BRONCE",
                "ORO",
                80,
                "VALORANT",
                "HAVEN",
                "Confirmado",
                host,
                new ArrayList<>() {{
                    add(host);
                    add(guest);
                }},
                new HashSet<>()
        );

        when(notifierFactory.createDiscordNotifier()).thenReturn(discordNotifier);
        when(notifierFactory.createEmailNotifier()).thenReturn(emailNotifier);
        when(notifierFactory.createPushNotifier()).thenReturn(pushNotifier);
        when(notifierFactory.createICalNotifier()).thenReturn(iCalNotifier);
        when(lobbyRepository.findById(lobbyId)).thenReturn(Optional.of(lobby));

        NotificationSubscriber subscriber = new NotificationSubscriber(notifierFactory, lobbyRepository);
        subscriber.onDomainEvent(new ScrimStateChangedEvent(this, lobbyId, "Confirmado"));

        verify(emailNotifier, times(1)).sendNotification(eq("host@test.com"), contains("Confirmado"));
        verify(emailNotifier, times(1)).sendNotification(eq("guest@test.com"), contains("Confirmado"));
        verify(pushNotifier, times(1)).sendNotification(eq("host"), contains("Confirmado"));
        verify(pushNotifier, times(1)).sendNotification(eq("guest"), contains("Confirmado"));
        verify(discordNotifier, times(1)).sendNotification(eq("#scrim-updates"), contains("Confirmado"));
        verify(iCalNotifier, times(1)).sendNotification(eq("calendar@scrims.local"), contains("Confirmado"));
    }
}

