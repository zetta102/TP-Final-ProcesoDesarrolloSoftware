package com.pds.tp.application.facade;

import com.pds.tp.application.command.CommandExecutor;
import com.pds.tp.application.command.SwapJugadoresCommand;
import com.pds.tp.application.dto.*;
import com.pds.tp.application.service.ReportService;
import com.pds.tp.application.service.ScrimService;
import com.pds.tp.domain.entity.Lobby;
import com.pds.tp.domain.entity.Player;
import com.pds.tp.domain.entity.Scrim;
import com.pds.tp.domain.entity.ScrimStatistics;
import com.pds.tp.domain.state.ScrimContext;
import com.pds.tp.domain.state.ScrimStateResolver;
import com.pds.tp.infrastructure.repository.LobbyRepository;
import com.pds.tp.infrastructure.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ScrimFacade {
    private final ScrimService scrimService;
    private final ReportService reportService;
    private final CommandExecutor commandExecutor;
    private final LobbyRepository lobbyRepository;
    private final PlayerRepository playerRepository;
    private final ScrimStateResolver stateResolver;

    public ScrimFacade(ScrimService scrimService,
                       ReportService reportService,
                       CommandExecutor commandExecutor,
                       LobbyRepository lobbyRepository,
                       PlayerRepository playerRepository,
                       ScrimStateResolver stateResolver) {
        this.scrimService = scrimService;
        this.reportService = reportService;
        this.commandExecutor = commandExecutor;
        this.lobbyRepository = lobbyRepository;
        this.playerRepository = playerRepository;
        this.stateResolver = stateResolver;
    }

    public Lobby createScrim(CreateScrimRequest request) {
        return scrimService.createScrim(request);
    }

    public List<Lobby> findScrims(String juego, String region, String rangoMin, String rangoMax, String fecha, Integer latenciaMax) {
        FindLobbyData findLobbyData = new FindLobbyData(juego, region, rangoMin, rangoMax, fecha, latenciaMax);
        return scrimService.findActiveLobbiesByRegionAndRank(findLobbyData);
    }

    public LobbyConfirmation applyToScrim(String id, ApplyToScrimRequest request) {
        LobbyApplication lobbyApplication = new LobbyApplication(request.username(), id, request.desiredRole());
        return scrimService.applyToLobby(lobbyApplication);
    }

    public String confirmParticipation(String id, ConfirmParticipationRequest request) {
        return scrimService.confirmarParticipacion(UUID.fromString(id), request.username());
    }

    public String executeCommand(String id, String command, SwapPlayersRequest request) {
        if (!"swap".equals(command)) {
            throw new IllegalArgumentException("Comando no soportado: " + command);
        }

        Lobby lobby = lobbyRepository.getReferenceById(UUID.fromString(id));
        Player p1 = playerRepository.findByUsername(request.jugador1());
        Player p2 = playerRepository.findByUsername(request.jugador2());

        ScrimContext context = new ScrimContext(lobby, stateResolver.resolve(lobby.getStatus()), null);
        SwapJugadoresCommand swapCommand = new SwapJugadoresCommand(p1, p2);
        commandExecutor.executeCommand(swapCommand, context);

        return "Swap ejecutado con exito.";
    }

    public Scrim startScrim(String id) {
        return scrimService.startScrim(new ScrimData(id));
    }

    public String cancelScrim(String id) {
        return scrimService.cancelLobbyById(UUID.fromString(id));
    }

    public String finishScrim(String id) {
        return scrimService.finishScrimById(UUID.fromString(id));
    }

    public ReportConfirmation reportPlayer(String id, ReportApplication request) {
        ReportApplication reportApplication = new ReportApplication(
                request.reportingPlayerUsername(),
                id,
                request.reportedPlayerUsername(),
                request.reason()
        );
        return reportService.processReport(reportApplication);
    }

    public ScrimStatistics saveStatistics(String id, CreateStatisticsRequest request) {
        return scrimService.saveStatistics(UUID.fromString(id), request);
    }
}

