package com.pds.tp.domain.state;

import com.pds.tp.domain.entity.Lobby;
import com.pds.tp.domain.entity.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.pds.tp.support.TestFixtures.lobby;
import static com.pds.tp.support.TestFixtures.player;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OperationalScrimStateTest {

    @Test
    void confirmedStateShouldKeepValidationMessagesAndAllowedTransitions() {
        ScrimContext context = new ScrimContext(buildLobby(), new ConfirmedState(), null);
        Player candidate = player("p1", "p1@test.com", "LAS");

        assertEquals("El lobby está completo y ya fue confirmado.", assertThrows(IllegalStateException.class, () -> context.postular(candidate, "FLEX")).getMessage());
        assertEquals("Todos los jugadores ya confirmaron.", assertThrows(IllegalStateException.class, () -> context.confirmar(candidate)).getMessage());
        assertEquals("El scrim debe iniciar antes de poder finalizarlo.", assertThrows(IllegalStateException.class, context::finalizar).getMessage());

        context.cancelar();
        assertEquals("Cancelado", context.getState().getStatusName());
    }

    @Test
    void playingStateShouldKeepValidationMessagesAndAllowedTransitions() {
        ScrimContext context = new ScrimContext(buildLobby(), new PlayingState(), null);
        Player candidate = player("p2", "p2@test.com", "LAS");

        assertEquals("El scrim está en curso.", assertThrows(IllegalStateException.class, () -> context.postular(candidate, "FLEX")).getMessage());
        assertEquals("El scrim está en curso.", assertThrows(IllegalStateException.class, () -> context.confirmar(candidate)).getMessage());
        assertEquals("El scrim ya fue iniciado.", assertThrows(IllegalStateException.class, context::iniciar).getMessage());
        assertEquals("No se puede cancelar un scrim en curso; debe finalizarse.", assertThrows(IllegalStateException.class, context::cancelar).getMessage());

        context.finalizar();
        assertEquals("Finalizado", context.getState().getStatusName());
    }

    private Lobby buildLobby() {
        Player host = player("host", "host@test.com", "LAS");
        return lobby(host, 1, 2, "Buscando", "BRONCE", "ORO", "ASCENT", List.of(host));
    }
}
