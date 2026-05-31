package com.pds.tp.controller;

import com.pds.tp.application.facade.ScrimFacade;
import com.pds.tp.application.dto.ApplyToScrimRequest;
import com.pds.tp.application.dto.ConfirmParticipationRequest;
import com.pds.tp.application.dto.CreateScrimRequest;
import com.pds.tp.application.dto.CreateStatisticsRequest;
import com.pds.tp.application.dto.LobbyConfirmation;
import com.pds.tp.application.dto.ReportApplication;
import com.pds.tp.application.dto.ReportConfirmation;
import com.pds.tp.application.dto.SwapPlayersRequest;
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
            @RequestParam String region,
            @RequestParam String rangoMin,
            @RequestParam String rangoMax,
            @RequestParam(required = false) String fecha,
            @RequestParam int latenciaMax) {
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
