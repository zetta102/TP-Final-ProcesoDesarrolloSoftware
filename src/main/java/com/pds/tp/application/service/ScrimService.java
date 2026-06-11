package com.pds.tp.application.service;

import com.pds.tp.application.dto.*;
import com.pds.tp.domain.builder.ScrimBuilder;
import com.pds.tp.domain.entity.*;
import com.pds.tp.domain.event.ScrimCreatedEvent;
import com.pds.tp.domain.shared.RankScale;
import com.pds.tp.domain.validation.GameValidatorFactory;
import com.pds.tp.domain.state.ScrimContext;
import com.pds.tp.domain.state.ScrimStateResolver;
import com.pds.tp.domain.strategy.MatchmakingStrategy;
import com.pds.tp.domain.valueobject.WaitlistStatus;
import com.pds.tp.infrastructure.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ScrimService {
    private static final String STATUS_BUSCANDO = "Buscando";
    private static final String STATUS_CONFIRMADO = "Confirmado";
    private static final String STATUS_EN_JUEGO = "EnJuego";
    private static final String STATUS_FINALIZADO = "Finalizado";

    private final ScrimRepository scrimRepository;
    private final LobbyRepository lobbyRepository;
    private final PlayerRepository playerRepository;
    private final ScrimStatisticsRepository scrimStatisticsRepository;
    private final PlayerScrimStatsRepository playerScrimStatsRepository;
    private final WaitlistRepository waitlistRepository;

    private final ApplicationEventPublisher eventPublisher;
    private final MatchmakingStrategy matchmakingStrategy;
    private final ScrimStateResolver stateResolver;
    private final GameValidatorFactory gameValidatorFactory;
    private final MMRRecalculator mmrRecalculator;

    public ScrimService(ScrimRepository scrimRepository, LobbyRepository lobbyRepository,
                        PlayerRepository playerRepository, ScrimStatisticsRepository scrimStatisticsRepository,
                        PlayerScrimStatsRepository playerScrimStatsRepository,
                        WaitlistRepository waitlistRepository,
                        ApplicationEventPublisher eventPublisher,
                        MatchmakingStrategy matchmakingStrategy,
                        ScrimStateResolver stateResolver,
                        GameValidatorFactory gameValidatorFactory,
                        MMRRecalculator mmrRecalculator) {
        this.scrimRepository = scrimRepository;
        this.lobbyRepository = lobbyRepository;
        this.playerRepository = playerRepository;
        this.scrimStatisticsRepository = scrimStatisticsRepository;
        this.playerScrimStatsRepository = playerScrimStatsRepository;
        this.waitlistRepository = waitlistRepository;
        this.eventPublisher = eventPublisher;
        this.matchmakingStrategy = matchmakingStrategy;
        this.stateResolver = stateResolver;
        this.gameValidatorFactory = gameValidatorFactory;
        this.mmrRecalculator = mmrRecalculator;
    }


    public Lobby createScrim(CreateScrimRequest request) {
        if (request.playersPerSide() <= 0 || request.playersPerSide() > 5) {
            throw new IllegalArgumentException("Los jugadores por lado deben estar entre 1 y 5.");
        }

        int minPlayers = request.playersPerSide();
        int maxPlayers = request.totalPlayers() > 0
                ? request.totalPlayers()
                : request.playersPerSide() * 2;

        if (maxPlayers < 2 || maxPlayers > 10) {
            throw new IllegalArgumentException("El total de jugadores debe estar entre 2 y 10.");
        }

        Player host = playerRepository.findByUsername(request.hostUserName());
        LocalDateTime scheduledTime = parseScheduledDate(request.scheduledDate());

        Lobby lobby = new ScrimBuilder()
                .host(host)
                .fecha(scheduledTime)
                .region(request.region())
                .formato(minPlayers, maxPlayers)
                .rango(request.minRank(), request.maxRank())
                .juego(request.game() != null ? request.game() : request.format(), request.map())
                .latenciaMax(request.maxLatency())
                .duracion(request.duration())
                .modalidad(request.mode())
                .validator(gameValidatorFactory.resolve(request.game()))
                .build();

        Lobby savedLobby = lobbyRepository.save(lobby);
        eventPublisher.publishEvent(new ScrimCreatedEvent(this, savedLobby.getId(), savedLobby.getGameMode(), savedLobby.getRegion()));
        return savedLobby;
    }

    public LobbyConfirmation applyToLobby(LobbyApplication lobbyApplication) {
        Lobby lobby = lobbyRepository.getReferenceById(UUID.fromString(lobbyApplication.lobbyId()));
        Player player = playerRepository.findByUsername(lobbyApplication.username());

        if (lobby.getPlayers().size() >= lobby.getMaxPlayers() && !lobby.getPlayers().contains(player)) {
            enqueueWaitlistIfNeeded(lobby, player, lobbyApplication.desiredRole());
            return new LobbyConfirmation(
                    player.getId().toString(),
                    lobby.getId().toString(),
                    "En lista de espera",
                    "El lobby está lleno. Jugador agregado a la lista de espera."
            );
        }

        ScrimContext context = new ScrimContext(lobby, stateResolver.resolve(lobby.getStatus()), eventPublisher);
        try {
            context.postular(player, lobbyApplication.desiredRole());
            lobbyRepository.save(lobby);
            return new LobbyConfirmation(player.getId().toString(), lobby.getId().toString(), "Aceptado", "Unido al lobby exitosamente.");
        } catch (IllegalStateException e) {
            log.info("Postulación rechazada: {}", e.getMessage());
            return new LobbyConfirmation(lobbyApplication.username(), lobbyApplication.lobbyId(), "Rechazado", e.getMessage());
        }
    }

    public String confirmParticipation(UUID lobbyId, String username) {
        Lobby lobby = lobbyRepository.getReferenceById(lobbyId);
        Player player = playerRepository.findByUsername(username);

        ScrimContext context = new ScrimContext(lobby, stateResolver.resolve(lobby.getStatus()), eventPublisher);
        try {
            context.confirmar(player);
            lobbyRepository.save(lobby);
            return "Jugador confirmado exitosamente.";
        } catch (IllegalStateException e) {
            return "Error de confirmación: " + e.getMessage();
        }
    }


    public Scrim startScrim(ScrimData scrimData) {
        Lobby lobby = lobbyRepository.getReferenceById(UUID.fromString(scrimData.lobbyId()));

        return scrimRepository.findByLobbyId(lobby)
                .orElseGet(() -> createAndPersistScrim(lobby));
    }

    private Scrim createAndPersistScrim(Lobby lobby) {
        ScrimContext context = new ScrimContext(lobby, stateResolver.resolve(lobby.getStatus()), eventPublisher);

        // State transition validation is delegated to the current ScrimState.
        context.iniciar();
        lobbyRepository.save(lobby);

        Scrim scrim = scrimRepository.save(new Scrim(lobby, lobby.getGameMode(), lobby.getMap(), context.getState().getStatusName()));

        List<Player> teamRed = lobby.getPlayers().subList(0, lobby.getPlayers().size() / 2);
        List<Player> teamBlue = lobby.getPlayers().subList(lobby.getPlayers().size() / 2, lobby.getPlayers().size());
        ScrimStatistics stats = new ScrimStatistics(scrim, teamRed, teamBlue);
        scrimStatisticsRepository.save(stats);

        return scrim;
    }

    public int autoStartConfirmedLobbies(LocalDateTime now) {
        List<Lobby> lobbiesToStart = lobbyRepository
                .findAllByStatusEqualsAndScheduledTimeLessThanEqual(STATUS_CONFIRMADO, now);

        int started = 0;
        for (Lobby lobby : lobbiesToStart) {
            if (scrimRepository.findByLobbyId(lobby).isPresent()) {
                continue;
            }

            try {
                createAndPersistScrim(lobby);
                started++;
            } catch (IllegalStateException ex) {
                log.warn("El scheduler no pudo iniciar el lobby {}: {}", lobby.getId(), ex.getMessage());
            }
        }

        return started;
    }

    public int autoFinalizeRunningScrims(LocalDateTime now, long maxDurationHours) {
        List<Scrim> runningScrims = scrimRepository.findAllByStatusEquals(STATUS_EN_JUEGO);
        int finalized = 0;

        for (Scrim scrim : runningScrims) {
            if (scrim.getStartTime() == null) {
                continue;
            }

            long runningHours = ChronoUnit.HOURS.between(scrim.getStartTime(), now);
            if (runningHours < maxDurationHours) {
                continue;
            }

            try {
                finishScrimById(scrim.getId());
                finalized++;
            } catch (IllegalStateException ex) {
                log.warn("El scheduler no pudo finalizar el scrim {}: {}", scrim.getId(), ex.getMessage());
            }
        }

        return finalized;
    }

    public String cancelLobbyById(UUID lobbyId, String reason) {
        Lobby lobby = lobbyRepository.getReferenceById(lobbyId);
        ScrimContext context = new ScrimContext(lobby, stateResolver.resolve(lobby.getStatus()), eventPublisher);

        try {
            // Apply late-cancellation penalty if lobby is Confirmado and close to scheduled time
            applyLateCancellationPenalty(lobby);

            lobby.setCancelReason(reason);
            context.cancelar();
            lobbyRepository.save(lobby);
            return "Lobby " + lobbyId + " cancelado." + (reason != null ? " Motivo: " + reason : "");
        } catch (IllegalStateException e) {
            return e.getMessage();
        }
    }

    private void applyLateCancellationPenalty(Lobby lobby) {
        if (!"Confirmado".equals(lobby.getStatus())) {
            return;
        }
        if (lobby.getScheduledTime() == null || lobby.getHost() == null) {
            return;
        }

        long hoursUntilStart = ChronoUnit.HOURS.between(LocalDateTime.now(), lobby.getScheduledTime());
        if (hoursUntilStart < 1) {
            Player host = lobby.getHost();
            host.setStrikes(host.getStrikes() + 1);
            log.warn("Penalización aplicada al organizador {} por cancelación tardía. Strikes: {}",
                    host.getUsername(), host.getStrikes());

            if (host.getStrikes() >= 3) {
                host.setBanned(true);
                log.warn("Usuario {} baneado por acumulación de strikes ({})",
                        host.getUsername(), host.getStrikes());
            }
            playerRepository.save(host);
        }
    }

    public String finishScrimById(UUID scrimId) {
        Scrim scrim = scrimRepository.getReferenceById(scrimId);
        Lobby lobby = scrim.getLobbyId();
        ScrimContext context = new ScrimContext(lobby, stateResolver.resolve(lobby.getStatus()), eventPublisher);

        try {
            context.finalizar();
            lobbyRepository.save(lobby);

            scrim.setStatus(context.getState().getStatusName());
            scrimRepository.save(scrim);

            ScrimStatistics stats = scrimStatisticsRepository.findByScrimId(scrim);
            stats.setEndTime(LocalDateTime.now());
            stats.setStatus(STATUS_FINALIZADO);
            scrimStatisticsRepository.save(stats);

            return "Scrim finalizado exitosamente.";
        } catch (IllegalStateException e) {
            return e.getMessage();
        }
    }

    public List<Lobby> findActiveLobbiesByRegionAndRank(FindLobbyData data) {
        return lobbyRepository.findAllByStatusEquals(STATUS_BUSCANDO)
                .stream()
                .filter(lobby -> data.game() == null || data.game().isBlank() || lobby.getGameMode().equalsIgnoreCase(data.game()))
                .filter(lobby -> data.region() == null || data.region().isBlank() || lobby.getRegion().equalsIgnoreCase(data.region()))
                .filter(lobby -> data.minRank() == null || data.minRank().isBlank() || compareRanks(lobby.getMinRank(), data.minRank()) <= 0)
                .filter(lobby -> data.maxRank() == null || data.maxRank().isBlank() || compareRanks(lobby.getMaxRank(), data.maxRank()) >= 0)
                .filter(lobby -> data.maxLatency() == null || lobby.getMaxPing() <= data.maxLatency())
                .filter(lobby -> data.date() == null || data.date().isBlank() || isSameDay(lobby, data.date()))
                .toList();
    }

    public void runMatchmakingPass() {
        List<Lobby> lobbies = lobbyRepository.findAllByStatusEquals(STATUS_BUSCANDO);
        // Use the full player pool as matchmaking candidates for this scheduled prototype flow.
        List<Player> availablePlayers = playerRepository.findAll();

        for (Lobby lobby : lobbies) {
            promoteWaitlistedPlayers(lobby);

            List<Player> selected = matchmakingStrategy.seleccionar(availablePlayers, lobby);

            ScrimContext context = new ScrimContext(lobby, stateResolver.resolve(lobby.getStatus()), eventPublisher);
            for (Player p : selected) {
                try {
                    context.postular(p, "FLEX");
                } catch (IllegalStateException ignored) {
                    // A selected player can become invalid while the lobby is being filled.
                }
            }
            lobbyRepository.save(lobby);
        }
    }

    private void enqueueWaitlistIfNeeded(Lobby lobby, Player player, String desiredRole) {
        waitlistRepository.findFirstByLobbyAndPlayerAndStatus(lobby, player, WaitlistStatus.PENDING)
                .orElseGet(() -> waitlistRepository.save(new Waitlist(lobby, player, desiredRole)));
    }

    private void promoteWaitlistedPlayers(Lobby lobby) {
        int availableSlots = lobby.getMaxPlayers() - lobby.getPlayers().size();
        if (availableSlots <= 0) {
            return;
        }

        List<Waitlist> pendingEntries = waitlistRepository.findAllByLobbyAndStatusOrderByCreatedAtAsc(lobby, WaitlistStatus.PENDING);
        if (pendingEntries.isEmpty()) {
            return;
        }

        ScrimContext context = new ScrimContext(lobby, stateResolver.resolve(lobby.getStatus()), eventPublisher);
        for (Waitlist entry : pendingEntries) {
            if (availableSlots <= 0) {
                break;
            }

            try {
                context.postular(entry.getPlayer(), entry.getDesiredRole() != null ? entry.getDesiredRole() : "FLEX");
                entry.setStatus(WaitlistStatus.PROMOTED);
                entry.setPromotedAt(LocalDateTime.now());
                waitlistRepository.save(entry);
                availableSlots--;
            } catch (IllegalStateException ignored) {
                // Keep entry pending if current state/rules still block promotion.
            }
        }
    }

    public ScrimStatistics getStatistics(UUID scrimId) {
        return scrimStatisticsRepository.findByScrimId(scrimRepository.getReferenceById(scrimId));
    }

    public ScrimStatistics saveStatistics(UUID scrimId, CreateStatisticsRequest request) {
        Scrim scrim = scrimRepository.getReferenceById(scrimId);
        ScrimStatistics statistics = scrimStatisticsRepository.findByScrimId(scrim);

        if (request.winningTeam() != null && !request.winningTeam().isBlank()) {
            statistics.setWinningTeam(request.winningTeam());
        }
        if (request.status() != null && !request.status().isBlank()) {
            statistics.setStatus(request.status());
        }

        scrimStatisticsRepository.save(statistics);

        // Persist per-player stats and recalculate MMR
        List<PlayerScrimStats> playerStatsList = new ArrayList<>();
        if (request.playerStats() != null && !request.playerStats().isEmpty()) {
            for (CreateStatisticsRequest.PlayerStatsEntry entry : request.playerStats()) {
                Player player = playerRepository.findByUsername(entry.username());
                if (player != null) {
                    PlayerScrimStats pss = new PlayerScrimStats(
                            statistics, player, entry.kills(), entry.deaths(), entry.assists(), entry.mvp());
                    playerScrimStatsRepository.save(pss);
                    playerStatsList.add(pss);
                }
            }

            // Trigger MMR recalculation if winning team is known
            if (request.winningTeam() != null && !request.winningTeam().isBlank()) {
                mmrRecalculator.recalculate(playerStatsList, request.winningTeam(),
                        statistics.getRedTeam(), statistics.getBlueTeam());
            }
        }

        return statistics;
    }

    private int compareRanks(String a, String b) {
        return RankScale.toValue(a) - RankScale.toValue(b);
    }

    private boolean isSameDay(Lobby lobby, String fecha) {
        try {
            LocalDateTime requested = LocalDateTime.parse(fecha);
            return lobby.getScheduledTime() != null && lobby.getScheduledTime().toLocalDate().isEqual(requested.toLocalDate());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Formato de fecha inválido en el filtro. Use ISO-8601, por ejemplo 2026-06-18T21:00:00.");
        }
    }

    private LocalDateTime parseScheduledDate(String rawDate) {
        if (rawDate == null || rawDate.isBlank()) {
            return LocalDateTime.now();
        }

        try {
            return LocalDateTime.parse(rawDate);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Formato de fecha inválido. Use ISO-8601, por ejemplo 2026-06-18T21:00:00.");
        }
    }
}

