package com.pds.tp.application.command;

import com.pds.tp.domain.entity.Player;
import com.pds.tp.domain.state.ScrimContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class SwapJugadoresCommand implements ScrimCommand {

    private final Player player1;
    private final Player player2;

    public SwapJugadoresCommand(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    @Override
    public void execute(ScrimContext ctx) {
        // En una implementación completa, aquí se intercambiarían los roles/lados (Ej: Equipo Rojo a Equipo Azul)
        // Para este prototipo, verificamos que ambos existan y registramos la acción.
        List<Player> lobbyPlayers = ctx.getLobby().getPlayers();
        if (lobbyPlayers.contains(player1) && lobbyPlayers.contains(player2)) {
            log.info("COMMAND EXECUTE: Intercambiando posiciones de {} y {}", player1.getUsername(), player2.getUsername());
        } else {
            throw new IllegalStateException("Ambos jugadores deben pertenecer al lobby para hacer swap.");
        }
    }

    @Override
    public void undo(ScrimContext ctx) {
        log.info("COMMAND UNDO: Revirtiendo posiciones de {} y {}", player1.getUsername(), player2.getUsername());
    }
}

