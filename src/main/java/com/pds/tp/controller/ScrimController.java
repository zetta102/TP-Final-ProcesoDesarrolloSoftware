package com.pds.tp.controller;

import com.pds.tp.entity.Lobby;
import com.pds.tp.entity.Scrim;
import com.pds.tp.entity.ScrimStatistics;
import com.pds.tp.model.*;
import com.pds.tp.service.ScrimService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/api/scrims")
public class ScrimController {
    private final ScrimService scrimService;

    public ScrimController(ScrimService scrimService) {
        this.scrimService = scrimService;
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

    // MISSING ENDPOINT ADDED: Confirmación requerida antes de EnJuego
    @PostMapping("/{id}/confirmaciones")
    public ResponseEntity<Map<String, String>> confirmarParticipacion(
            @PathVariable String id,
            @RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String result = scrimService.confirmarParticipacion(UUID.fromString(id), username);
        return ResponseEntity.ok(Map.of("mensaje", result));
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

    @PostMapping("/reportes")
    public ResponseEntity<ReportConfirmation> reportPlayer(@RequestBody ReportApplication reportApplication) {
        return ResponseEntity.status(201).body(scrimService.reportPlayer(reportApplication));
    }

    @GetMapping("/scrimStatistics")
    public ResponseEntity<ScrimStatistics> stats(@RequestBody ScrimData scrimStatisticsData) {
        return ResponseEntity.ok(scrimService.getStatistics(UUID.fromString(scrimStatisticsData.lobbyId())));
    }
}