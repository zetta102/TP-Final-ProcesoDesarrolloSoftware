package com.pds.tp.domain.state;

import com.pds.tp.domain.entity.Lobby;
import com.pds.tp.domain.entity.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.pds.tp.support.TestFixtures.lobby;
import static com.pds.tp.support.TestFixtures.player;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TerminalScrimStateTest {

    @Test
    void canceledStateShouldRejectEveryOperation() {
        ScrimContext context = new ScrimContext(buildLobby(), new CanceledState(), null);
        Player candidate = player("p1", "p1@test.com", "LAS");

        assertEquals("El scrim fue cancelado.", assertThrows(IllegalStateException.class, () -> context.postular(candidate, "FLEX")).getMessage());
        assertEquals("El scrim fue cancelado.", assertThrows(IllegalStateException.class, () -> context.confirmar(candidate)).getMessage());
        assertEquals("El scrim fue cancelado.", assertThrows(IllegalStateException.class, context::iniciar).getMessage());
        assertEquals("El scrim fue cancelado.", assertThrows(IllegalStateException.class, context::finalizar).getMessage());
        assertEquals("El scrim ya fue cancelado.", assertThrows(IllegalStateException.class, context::cancelar).getMessage());
    }

    @Test
    void finishedStateShouldRejectEveryOperation() {
        ScrimContext context = new ScrimContext(buildLobby(), new FinishedState(), null);
        Player candidate = player("p2", "p2@test.com", "LAS");

        assertEquals("El scrim ya fue finalizado.", assertThrows(IllegalStateException.class, () -> context.postular(candidate, "FLEX")).getMessage());
        assertEquals("El scrim ya fue finalizado.", assertThrows(IllegalStateException.class, () -> context.confirmar(candidate)).getMessage());
        assertEquals("El scrim ya fue finalizado.", assertThrows(IllegalStateException.class, context::iniciar).getMessage());
        assertEquals("El scrim ya fue finalizado.", assertThrows(IllegalStateException.class, context::finalizar).getMessage());
        assertEquals("El scrim ya fue finalizado.", assertThrows(IllegalStateException.class, context::cancelar).getMessage());
    }

    private Lobby buildLobby() {
        Player host = player("host", "host@test.com", "LAS");
        return lobby(host, 1, 2, "Buscando", "BRONCE", "ORO", "ASCENT", List.of(host));
    }
}
