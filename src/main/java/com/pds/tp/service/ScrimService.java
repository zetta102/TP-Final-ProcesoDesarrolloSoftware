package com.pds.tp.service;


import com.pds.tp.entity.*;
import com.pds.tp.model.*;
import com.pds.tp.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class ScrimService {
    private final LobbyRepository lobbyRepository;
    private final PlayerRepository playerRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MatchmakingStrategy matchmakingStrategy;

    public ScrimService(LobbyRepository lobbyRepository, PlayerRepository playerRepository,
                        ApplicationEventPublisher eventPublisher, MatchmakingStrategy matchmakingStrategy) {
        this.lobbyRepository = lobbyRepository;
        this.playerRepository = playerRepository;
        this.eventPublisher = eventPublisher;
        this.matchmakingStrategy = matchmakingStrategy;
    }

    public LobbyConfirmation applyToLobby(LobbyApplication lobbyApplication) {
        Lobby lobby = lobbyRepository.getReferenceById(UUID.fromString(lobbyApplication.lobbyId()));
        Player player = playerRepository.findByUsername(lobbyApplication.username());

        // Hydrate the state pattern context
        ScrimContext context = new ScrimContext(lobby, hydrateState(lobby.getStatus()));

        try {
            // Delegate the rule processing to the State pattern
            context.postular(player, lobbyApplication.desiredRole());
            lobbyRepository.save(lobby);

            // Trigger Observer if state changed
            eventPublisher.publishEvent(new ScrimStateChangedEvent(this, lobby.getId(), lobby.getStatus()));

            return new LobbyConfirmation(player.getId().toString(), lobby.getId().toString(), "Confirmed", "Action successful");
        } catch (IllegalStateException e) {
            log.warn("Application failed: {}", e.getMessage());
            return new LobbyConfirmation(player.getId().toString(), lobby.getId().toString(), "Rejected", e.getMessage());
        }
    }

    // Helper to map DB String to State Object
    private ScrimState hydrateState(String status) {
        return switch (status) {
            case "Buscando" -> new BuscandoState();
            // case "LobbyArmado" -> new LobbyArmadoState();
            // case "Confirmado" -> new ConfirmadoState();
            default -> new BuscandoState();
        };
    }
}