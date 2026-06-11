package com.pds.tp.infrastructure.notification;

import com.pds.tp.domain.entity.Lobby;
import com.pds.tp.domain.entity.Player;
import com.pds.tp.domain.entity.SavedSearch;
import com.pds.tp.domain.event.ScrimCreatedEvent;
import com.pds.tp.domain.event.ScrimStateChangedEvent;
import com.pds.tp.infrastructure.repository.LobbyRepository;
import com.pds.tp.infrastructure.repository.SavedSearchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Observer that reacts to scrim domain events and fans them out through player-enabled channels only.
 */
@Slf4j
@Component
public class NotificationSubscriber {
    private static final int MAX_ATTEMPTS = 3;
    private static final long INITIAL_BACKOFF_MS = 200L;

    private final NotifierFactory notifierFactory;
    private final LobbyRepository lobbyRepository;
    private final SavedSearchRepository savedSearchRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    public NotificationSubscriber(NotifierFactory notifierFactory, LobbyRepository lobbyRepository,
                                  SavedSearchRepository savedSearchRepository,
                                  KafkaEventPublisher kafkaEventPublisher) {
        this.notifierFactory = notifierFactory;
        this.lobbyRepository = lobbyRepository;
        this.savedSearchRepository = savedSearchRepository;
        this.kafkaEventPublisher = kafkaEventPublisher;
    }

    @EventListener
    public void onDomainEvent(ScrimStateChangedEvent event) {
        String message = String.format("El lobby %s cambió al estado: %s",
                event.getLobbyId(), event.getNuevoEstado());

        List<PlayerNotificationProfile> profiles = resolvePlayerProfiles(event.getLobbyId());

        // Always send a general Discord channel notification (broadcast, not user-specific).
        sendWithRetry(notifierFactory.createDiscordNotifier(), "#scrim-updates", message, "DISCORD");

        // Per-player notifications based on their enabled channels.
        for (PlayerNotificationProfile profile : profiles) {
            Set<String> channels = profile.enabledChannels();

            if (channels.contains("EMAIL") && profile.email() != null) {
                sendWithRetry(notifierFactory.createEmailNotifier(), profile.email(), message, "EMAIL");
            }
            if (channels.contains("PUSH")) {
                sendWithRetry(notifierFactory.createPushNotifier(), profile.username(), message, "PUSH");
            }
            if (channels.contains("ICAL") && profile.email() != null) {
                sendWithRetry(notifierFactory.createICalNotifier(), profile.email(), message, "ICAL");
            }
        }

        // Enqueue to Kafka for async downstream consumers.
        publishToKafka(event.getLobbyId().toString(), message);
    }

    @EventListener
    public void onScrimCreated(ScrimCreatedEvent event) {
        String message = String.format("Nuevo scrim creado (%s) en %s. Lobby: %s",
                event.getGame(), event.getRegion(), event.getLobbyId());

        // Broadcast notification to Discord channel
        sendWithRetry(notifierFactory.createDiscordNotifier(), "#scrim-updates", message, "DISCORD");

        // Notify players whose saved searches match this new scrim
        Lobby lobby = lobbyRepository.findById(event.getLobbyId()).orElse(null);
        if (lobby != null) {
            List<SavedSearch> allSearches = savedSearchRepository.findAll();
            Set<Player> notifiedPlayers = new HashSet<>();

            for (SavedSearch search : allSearches) {
                if (search.matchesLobby(lobby) && !notifiedPlayers.contains(search.getPlayer())) {
                    Player player = search.getPlayer();
                    notifiedPlayers.add(player);

                    String alertMessage = String.format("¡Nuevo scrim que coincide con tus preferencias! %s en %s. Lobby: %s",
                            event.getGame(), event.getRegion(), event.getLobbyId());

                    Set<String> channels = parseEnabledChannels(player.getEnabledNotificationChannels());
                    if (channels.contains("EMAIL") && player.getEmail() != null) {
                        sendWithRetry(notifierFactory.createEmailNotifier(), player.getEmail(), alertMessage, "EMAIL");
                    }
                    if (channels.contains("PUSH")) {
                        sendWithRetry(notifierFactory.createPushNotifier(), player.getUsername(), alertMessage, "PUSH");
                    }
                }
            }

            log.info("Alertas de búsquedas guardadas enviadas a {} jugadores", notifiedPlayers.size());
        } else {
            // Fallback: broadcast to all channels
            sendWithRetry(notifierFactory.createEmailNotifier(), "all-players@scrims.local", message, "EMAIL");
            sendWithRetry(notifierFactory.createPushNotifier(), "all-players", message, "PUSH");
        }

        sendWithRetry(notifierFactory.createICalNotifier(), "calendar@scrims.local", message, "ICAL");

        // Enqueue to Kafka for async downstream consumers.
        publishToKafka(event.getLobbyId().toString(), message);
    }

    private void publishToKafka(String key, String message) {
        try {
            kafkaEventPublisher.publish(key, message);
        } catch (Exception ex) {
            log.warn("Error al publicar evento en Kafka (no crítico): {}", ex.getMessage());
        }
    }

    private void sendWithRetry(Notifier notifier, String target, String message, String channel) {
        long backoff = INITIAL_BACKOFF_MS;
        int attempt = 1;

        while (attempt <= MAX_ATTEMPTS) {
            try {
                notifier.sendNotification(target, message);
                return;
            } catch (RuntimeException ex) {
                if (attempt == MAX_ATTEMPTS) {
                    log.error("No se pudo entregar notificación {} después de {} intentos", channel, MAX_ATTEMPTS, ex);
                    return;
                }

                log.warn("Entrega fallida vía {} (intento {}), reintentando...", channel, attempt, ex);
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    log.error("Reintento interrumpido para canal {}", channel, interruptedException);
                    return;
                }
                backoff *= 2;
                attempt++;
            }
        }
    }

    private List<PlayerNotificationProfile> resolvePlayerProfiles(UUID lobbyId) {
        return lobbyRepository.findById(lobbyId)
                .map(this::toProfiles)
                .orElseGet(() -> List.of(new PlayerNotificationProfile(
                        "all-players", "all-players@scrims.local", Set.of("PUSH", "EMAIL", "DISCORD", "ICAL")
                )));
    }

    private List<PlayerNotificationProfile> toProfiles(Lobby lobby) {
        List<PlayerNotificationProfile> profiles = new ArrayList<>();

        if (lobby.getPlayers() != null) {
            for (Player player : lobby.getPlayers()) {
                Set<String> channels = parseEnabledChannels(player.getEnabledNotificationChannels());
                String email = (player.getEmail() != null && !player.getEmail().isBlank()) ? player.getEmail() : null;
                String username = (player.getUsername() != null && !player.getUsername().isBlank()) ? player.getUsername() : "unknown";
                profiles.add(new PlayerNotificationProfile(username, email, channels));
            }
        }

        if (profiles.isEmpty()) {
            profiles.add(new PlayerNotificationProfile("all-players", "all-players@scrims.local",
                    Set.of("PUSH", "EMAIL", "DISCORD", "ICAL")));
        }

        return profiles;
    }

    private Set<String> parseEnabledChannels(String raw) {
        if (raw == null || raw.isBlank()) {
            // Default: all channels enabled.
            return Set.of("PUSH", "EMAIL", "DISCORD", "ICAL");
        }
        Set<String> channels = new LinkedHashSet<>();
        for (String part : raw.split(",")) {
            String trimmed = part.trim().toUpperCase();
            if (!trimmed.isEmpty()) {
                channels.add(trimmed);
            }
        }
        return channels.isEmpty() ? Set.of("PUSH", "EMAIL", "DISCORD", "ICAL") : channels;
    }

    private record PlayerNotificationProfile(String username, String email, Set<String> enabledChannels) {
    }
}
