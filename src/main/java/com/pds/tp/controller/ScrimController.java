package com.pds.tp.controller;

import com.pds.tp.entity.Lobby;
import com.pds.tp.entity.Scrim;
import com.pds.tp.model.FindLobbyData;
import com.pds.tp.model.LobbyData;
import com.pds.tp.model.ScrimData;
import com.pds.tp.service.ScrimService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController("v1/api/scrims")
public class ScrimController {
    private final ScrimService scrimService;

    public ScrimController(ScrimService scrimService) {
        this.scrimService = scrimService;
    }

    @PostMapping("/createLobby")
    public Lobby createLobby(@RequestBody LobbyData lobbyData) {
        return scrimService.createLobby(lobbyData);
    }

    @PostMapping("/startScrim")
    public Scrim create(@RequestBody ScrimData scrimData) {
        return scrimService.startScrim(scrimData);
    }

    @GetMapping("/findLobbies")
    public List<Lobby> find(
            @RequestBody FindLobbyData findLobbyData) {
        return scrimService.findActiveLobbiesByRegionAndRank(findLobbyData);
    }

    @PostMapping("/{id}/cancelLobby")
    public String cancel(@PathVariable String id) {
        return scrimService.cancelLobbyById(UUID.fromString(id));
    }

    @PostMapping("/{id}/finishScrim")
    public String end(@PathVariable String id) {
        return scrimService.finishScrimById(UUID.fromString(id));
    }


    //TODO: Implementar las siguientes funciones
    @PostMapping("/{id}/postulaciones")
    public ResponseEntity apply(@PathVariable String id) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/confirmaciones")
    public ResponseEntity confirm(@PathVariable String id) {
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{id}/estadisticas")
    public ResponseEntity stats(@PathVariable String id) {
        return ResponseEntity.ok().build();
    }
}
