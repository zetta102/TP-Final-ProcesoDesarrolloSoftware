package com.pds.tp.controller;

import com.pds.tp.application.command.CommandExecutor;
import com.pds.tp.application.command.SwapJugadoresCommand;
import com.pds.tp.application.dto.FindLobbyData;
import com.pds.tp.application.dto.LobbyApplication;
import com.pds.tp.application.dto.LobbyConfirmation;
import com.pds.tp.application.dto.LobbyData;
import com.pds.tp.application.dto.ReportApplication;
import com.pds.tp.application.dto.ReportConfirmation;
import com.pds.tp.application.dto.ScrimData;
import com.pds.tp.application.service.ReportService;
import com.pds.tp.application.service.ScrimService;
import com.pds.tp.domain.entity.Lobby;
import com.pds.tp.domain.entity.Player;
import com.pds.tp.domain.entity.Scrim;
import com.pds.tp.domain.entity.ScrimStatistics;
import com.pds.tp.domain.state.ScrimContext;
import com.pds.tp.infrastructure.repository.LobbyRepository;
import com.pds.tp.infrastructure.repository.PlayerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/api/scrims")
public class ScrimController {

    private final ScrimService scrimService;
    private final ReportService reportService;
    private final CommandExecutor commandExecutor;
    private final LobbyRepository lobbyRepository;
    private final PlayerRepository playerRepository;

    public ScrimController(ScrimService scrimService, ReportService reportService,
                           CommandExecutor commandExecutor, LobbyRepository lobbyRepository,
                           PlayerRepository playerRepository) {
        this.scrimService = scrimService;
        this.reportService = reportService;
        this.commandExecutor = commandExecutor;
        this.lobbyRepository = lobbyRepository;
        this.playerRepository = playerRepository;
    }

    @PostMapping("/createLobby")
    public ResponseEntity<Lobby> createLobby(@RequestBody LobbyData lobbyData) {
        return ResponseEntity.status(201).body(scrimService.createLobby(lobbyData));
    }

    @GetMapping("/findLobbies")
    public ResponseEntity<List<Lobby>> find(@RequestBody FindLobbyData findLobbyData) {
        return ResponseEntity.ok(scrimService.findActiveLobbiesByRegionAndRank(findLobbyData));
    }

    @PostMapping("/applyToLobby")
    public ResponseEntity<LobbyConfirmation> apply(@RequestBody LobbyApplication lobbyApplication) {
        return ResponseEntity.ok(scrimService.applyToLobby(lobbyApplication));
    }

    @PostMapping("/{id}/confirmaciones")
    public ResponseEntity<Map<String, String>> confirmarParticipacion(
            @PathVariable String id, @RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String result = scrimService.confirmarParticipacion(UUID.fromString(id), username);
        return ResponseEntity.ok(Map.of("mensaje", result));
    }

    // --- NUEVO: COMMAND PATTERN ENDPOINT (CU 6) ---
    @PostMapping("/{id}/acciones/swap")
    public ResponseEntity<Map<String, String>> swapJugadores(
            @PathVariable String id, @RequestBody Map<String, String> payload) {

        Lobby lobby = lobbyRepository.getReferenceById(UUID.fromString(id));
        Player p1 = playerRepository.findByUsername(payload.get("jugador1"));
        Player p2 = playerRepository.findByUsername(payload.get("jugador2"));

        ScrimContext ctx = new ScrimContext(lobby, null, null); // Dummy hydration for command action

        SwapJugadoresCommand command = new SwapJugadoresCommand(p1, p2);
        commandExecutor.executeCommand(command, ctx);

        return ResponseEntity.ok(Map.of("mensaje", "Swap ejecutado con éxito."));
    }

    @PostMapping("/startScrim")
    public ResponseEntity<Scrim> start(@RequestBody ScrimData scrimData) {
        return ResponseEntity.ok(scrimService.startScrim(scrimData));
    }

    @PostMapping("/{id}/cancelLobby")
    public ResponseEntity<Map<String, String>> cancel(@PathVariable String id) {
        String res = scrimService.cancelLobbyById(UUID.fromString(id));
        return ResponseEntity.ok(Map.of("mensaje", res));
    }

    @PostMapping("/{id}/finishScrim")
    public ResponseEntity<Map<String, String>> end(@PathVariable String id) {
        String res = scrimService.finishScrimById(UUID.fromString(id));
        return ResponseEntity.ok(Map.of("mensaje", res));
    }

    // --- ACTUALIZADO: CHAIN OF RESPONSIBILITY (CU 11) ---
    @PostMapping("/reportes")
    public ResponseEntity<ReportConfirmation> reportPlayer(@RequestBody ReportApplication reportApplication) {
        return ResponseEntity.status(201).body(reportService.processReport(reportApplication));
    }

    @GetMapping("/scrimStatistics")
    public ResponseEntity<ScrimStatistics> stats(@RequestBody ScrimData scrimStatisticsData) {
        return ResponseEntity.ok(scrimService.getStatistics(UUID.fromString(scrimStatisticsData.lobbyId())));
    }
}
