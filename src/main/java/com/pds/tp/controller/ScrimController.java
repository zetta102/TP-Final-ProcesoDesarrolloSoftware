package com.pds.tp.controller;

import com.pds.tp.application.dto.*;
import com.pds.tp.application.facade.ScrimFacade;
import com.pds.tp.domain.entity.Lobby;
import com.pds.tp.domain.entity.Scrim;
import com.pds.tp.domain.entity.ScrimStatistics;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/scrims", "/v1/api/scrims"})
public class ScrimController {
    private static final String MESSAGE_KEY = "mensaje";

    private final ScrimFacade scrimFacade;

    public ScrimController(ScrimFacade scrimFacade) {
        this.scrimFacade = scrimFacade;
    }

    @PostMapping
    public ResponseEntity<Lobby> createLobby(@RequestBody CreateScrimRequest request) {
        return ResponseEntity.status(201).body(scrimFacade.createScrim(request));
    }

    @GetMapping
    public ResponseEntity<List<Lobby>> find(
            @RequestParam(required = false) String juego,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String rangoMin,
            @RequestParam(required = false) String rangoMax,
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) Integer latenciaMax) {
        return ResponseEntity.ok(scrimFacade.findScrims(juego, region, rangoMin, rangoMax, fecha, latenciaMax));
    }

    @PostMapping("/{id}/postulaciones")
    public ResponseEntity<LobbyConfirmation> apply(@PathVariable String id, @RequestBody ApplyToScrimRequest request) {
        return ResponseEntity.ok(scrimFacade.applyToScrim(id, request));
    }

    @PostMapping("/{id}/confirmaciones")
    public ResponseEntity<Map<String, String>> confirmarParticipacion(
            @PathVariable String id, @RequestBody ConfirmParticipationRequest request) {
        return ResponseEntity.ok(Map.of(MESSAGE_KEY, scrimFacade.confirmParticipation(id, request)));
    }

    @PostMapping("/{id}/acciones/{command}")
    public ResponseEntity<Map<String, String>> executeCommand(
            @PathVariable String id,
            @PathVariable String command,
            @RequestBody SwapPlayersRequest payload) {
        try {
            return ResponseEntity.ok(Map.of(MESSAGE_KEY, scrimFacade.executeCommand(id, command, payload)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/iniciar")
    public ResponseEntity<Scrim> start(@PathVariable String id) {
        return ResponseEntity.ok(scrimFacade.startScrim(id));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Map<String, String>> cancel(@PathVariable String id) {
        return ResponseEntity.ok(Map.of(MESSAGE_KEY, scrimFacade.cancelScrim(id)));
    }

    @PostMapping("/{id}/finalizar")
    public ResponseEntity<Map<String, String>> end(@PathVariable String id) {
        return ResponseEntity.ok(Map.of(MESSAGE_KEY, scrimFacade.finishScrim(id)));
    }

    @PostMapping("/{id}/reportes")
    public ResponseEntity<ReportConfirmation> reportPlayer(
            @PathVariable String id,
            @RequestBody ReportApplication request) {
        return ResponseEntity.status(201).body(scrimFacade.reportPlayer(id, request));
    }

    @PostMapping("/{id}/estadisticas")
    public ResponseEntity<ScrimStatistics> saveStatistics(
            @PathVariable String id,
            @RequestBody CreateStatisticsRequest request) {
        return ResponseEntity.ok(scrimFacade.saveStatistics(id, request));
    }
}
