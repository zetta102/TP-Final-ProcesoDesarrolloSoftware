package com.pds.tp.application.service;

import com.pds.tp.application.dto.FindLobbyData;
import com.pds.tp.application.dto.LobbyApplication;
import com.pds.tp.application.dto.LobbyConfirmation;
import com.pds.tp.application.dto.LobbyData;
import com.pds.tp.application.dto.ReportApplication;
import com.pds.tp.application.dto.ReportConfirmation;
import com.pds.tp.application.dto.ScrimData;
import com.pds.tp.domain.builder.LobbyBuilder;
import com.pds.tp.domain.entity.Lobby;
import com.pds.tp.domain.entity.Player;
import com.pds.tp.domain.entity.Report;
import com.pds.tp.domain.entity.Scrim;
import com.pds.tp.domain.entity.ScrimStatistics;
import com.pds.tp.domain.state.CanceledState;
import com.pds.tp.domain.state.ConfirmedState;
import com.pds.tp.domain.state.CreatedLobbyState;
import com.pds.tp.domain.state.FinishedState;
import com.pds.tp.domain.state.PlayingState;
import com.pds.tp.domain.state.ScrimContext;
import com.pds.tp.domain.state.ScrimState;
import com.pds.tp.domain.state.SearchingState;
import com.pds.tp.domain.strategy.MatchmakingStrategy;
import com.pds.tp.infrastructure.repository.LobbyRepository;
import com.pds.tp.infrastructure.repository.PlayerRepository;
import com.pds.tp.infrastructure.repository.ReportRepository;
import com.pds.tp.infrastructure.repository.ScrimRepository;
import com.pds.tp.infrastructure.repository.ScrimStatisticsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ScrimService {
    private static final String STATUS_BUSCANDO = "Buscando";
    private static final String STATUS_FINALIZADO = "Finalizado";

    private final ScrimRepository scrimRepository;
    private final LobbyRepository lobbyRepository;
    private final PlayerRepository playerRepository;
    private final ScrimStatisticsRepository scrimStatisticsRepository;
    private final ReportRepository reportRepository;

    // Pattern Injections
    private final ApplicationEventPublisher eventPublisher;
    private final MatchmakingStrategy matchmakingStrategy;

    public ScrimService(ScrimRepository scrimRepository, LobbyRepository lobbyRepository,
                        PlayerRepository playerRepository, ScrimStatisticsRepository scrimStatisticsRepository,
                        ReportRepository reportRepository, ApplicationEventPublisher eventPublisher,
                        MatchmakingStrategy matchmakingStrategy) {
        this.scrimRepository = scrimRepository;
        this.lobbyRepository = lobbyRepository;
        this.playerRepository = playerRepository;
        this.scrimStatisticsRepository = scrimStatisticsRepository;
        this.reportRepository = reportRepository;
        this.eventPublisher = eventPublisher;
        this.matchmakingStrategy = matchmakingStrategy;
    }

    public Lobby createLobby(LobbyData lobbyData) {
        Player player = playerRepository.findByUsername(lobbyData.hostUserName());

        // Implementing the Builder Pattern
        Lobby lobby = new LobbyBuilder()
                .conHost(player)
                .conFormato(lobbyData.minPlayers(), lobbyData.maxPlayers())
                .conRango(lobbyData.minRank(), lobbyData.maxRank())
                .conJuego(lobbyData.gameMode(), lobbyData.map())
                .conLatenciaMax(lobbyData.maxPing())
                .build();

        return lobbyRepository.save(lobby);
    }

    public LobbyConfirmation applyToLobby(LobbyApplication lobbyApplication) {
        Lobby lobby = lobbyRepository.getReferenceById(UUID.fromString(lobbyApplication.lobbyId()));
        Player player = playerRepository.findByUsername(lobbyApplication.username());

        ScrimContext context = new ScrimContext(lobby, hydrateState(lobby.getStatus()), eventPublisher);
        try {
            context.postular(player, lobbyApplication.desiredRole());
            lobbyRepository.save(lobby);
            return new LobbyConfirmation(player.getId().toString(), lobby.getId().toString(), "Confirmed", "Unido al lobby exitosamente.");
        } catch (IllegalStateException e) {
            log.info("Application rejected: {}", e.getMessage());
            return new LobbyConfirmation(lobbyApplication.username(), lobbyApplication.lobbyId(), "Rejected", e.getMessage());
        }
    }

    public String confirmarParticipacion(UUID lobbyId, String username) {
        Lobby lobby = lobbyRepository.getReferenceById(lobbyId);
        Player player = playerRepository.findByUsername(username);

        ScrimContext context = new ScrimContext(lobby, hydrateState(lobby.getStatus()), eventPublisher);
        try {
            context.confirmar(player);
            lobbyRepository.save(lobby);
            return "Jugador confirmado con éxito.";
        } catch (IllegalStateException e) {
            return "Error al confirmar: " + e.getMessage();
        }
    }

    public Scrim startScrim(ScrimData scrimData) {
        Lobby lobby = lobbyRepository.getReferenceById(UUID.fromString(scrimData.lobbyId()));
        ScrimContext context = new ScrimContext(lobby, hydrateState(lobby.getStatus()), eventPublisher);

        context.iniciar(); // Delegates to State Pattern
        lobbyRepository.save(lobby);

        Scrim scrim = scrimRepository.save(new Scrim(lobby, lobby.getGameMode(), lobby.getMap(), context.getState().getStatusName()));

        List<Player> teamRed = lobby.getPlayers().subList(0, lobby.getPlayers().size() / 2);
        List<Player> teamBlue = lobby.getPlayers().subList(lobby.getPlayers().size() / 2, lobby.getPlayers().size());
        ScrimStatistics stats = new ScrimStatistics(scrim, teamRed, teamBlue);
        scrimStatisticsRepository.save(stats);

        return scrim;
    }

    public String cancelLobbyById(UUID lobbyId) {
        Lobby lobby = lobbyRepository.getReferenceById(lobbyId);
        ScrimContext context = new ScrimContext(lobby, hydrateState(lobby.getStatus()), eventPublisher);

        try {
            context.cancelar();
            lobbyRepository.save(lobby);
            return "Lobby " + lobbyId + " cancelado.";
        } catch (IllegalStateException e) {
            return e.getMessage();
        }
    }

    public String finishScrimById(UUID scrimId) {
        Scrim scrim = scrimRepository.getReferenceById(scrimId);
        Lobby lobby = scrim.getLobbyId();
        ScrimContext context = new ScrimContext(lobby, hydrateState(lobby.getStatus()), eventPublisher);

        try {
            context.finalizar();
            lobbyRepository.save(lobby);

            scrim.setStatus(context.getState().getStatusName());
            scrimRepository.save(scrim);

            ScrimStatistics stats = scrimStatisticsRepository.findByScrimId(scrim);
            stats.setEndTime(LocalDateTime.now());
            stats.setStatus(STATUS_FINALIZADO);
            scrimStatisticsRepository.save(stats);

            return "Scrim finalizada exitosamente.";
        } catch (IllegalStateException e) {
            return e.getMessage();
        }
    }

    public List<Lobby> findActiveLobbiesByRegionAndRank(FindLobbyData data) {
        // Here we could use MatchmakingStrategy to filter the lobbies dynamically
        // Currently wrapping the repo call, but Strategy Pattern is implemented above
        // to filter *candidates* during auto-matchmaking cycles.
        return lobbyRepository.findAllByRegionAndMinRankLessThanEqualAndMaxRankGreaterThanEqualAndMaxPingLessThanEqualAndStatusEquals(
                data.region(), data.minRank(), data.maxRank(), data.maxPing(), STATUS_BUSCANDO);
    }

    @Scheduled(fixedRate = 60, timeUnit = TimeUnit.SECONDS)
    public void autoMatchmakingCron() {
        List<Lobby> lobbies = lobbyRepository.findAllByStatusEquals(STATUS_BUSCANDO);
        List<Player> availablePlayers = playerRepository.findAll(); // Assuming pool of active lookups

        for (Lobby lobby : lobbies) {
            // Apply Strategy Pattern
            List<Player> selected = matchmakingStrategy.seleccionar(availablePlayers, lobby);

            ScrimContext context = new ScrimContext(lobby, hydrateState(lobby.getStatus()), eventPublisher);
            for (Player p : selected) {
                try {
                    context.postular(p, "FLEX");
                } catch (IllegalStateException ignored) {
                    // Some selected players may become invalid while the scheduler fills the lobby.
                }
            }
            lobbyRepository.save(lobby);
        }
    }

    public ReportConfirmation reportPlayer(ReportApplication reportApp) {
        // Existing implementation remains intact
        Player reportingPlayer = playerRepository.findByUsername(reportApp.reportingPlayerUsername());
        Player reportedPlayer = playerRepository.findByUsername(reportApp.reportedPlayerUsername());
        Scrim scrim = scrimRepository.getReferenceById(UUID.fromString(reportApp.lobbyId()));

        if (!scrim.getStatus().equals("Finalizado")) {
            throw new IllegalStateException("Scrim no finalizada.");
        }

        Report report = reportRepository.save(new Report(scrim, reportingPlayer, reportedPlayer, reportApp.reason(), ""));
        return new ReportConfirmation(report.getId(), report.getReportingPlayer().getUsername(), report.getScrimId().toString(), report.getReportedPlayer().getUsername(), report.getStatus());
    }

    public ScrimStatistics getStatistics(UUID scrimId) {
        return scrimStatisticsRepository.findByScrimId(scrimRepository.getReferenceById(scrimId));
    }

    // --- State Hydration Helper ---
    private ScrimState hydrateState(String status) {
        if (status == null) return new SearchingState();
        return switch (status) {
            case STATUS_BUSCANDO -> new SearchingState();
            case "LobbyArmado" -> new CreatedLobbyState();
            case "Confirmado" -> new ConfirmedState();
            case "EnJuego" -> new PlayingState();
            case STATUS_FINALIZADO -> new FinishedState();
            case "Cancelado" -> new CanceledState();
            default -> new SearchingState(); // Fallback
        };
    }
}

