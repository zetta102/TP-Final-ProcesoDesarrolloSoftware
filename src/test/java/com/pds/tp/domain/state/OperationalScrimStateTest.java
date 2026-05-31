package com.pds.tp.domain.state;

import com.pds.tp.domain.entity.Lobby;
import com.pds.tp.domain.entity.Player;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OperationalScrimStateTest {

    @Test
    void confirmedStateShouldKeepValidationMessagesAndAllowedTransitions() {
        ScrimContext context = new ScrimContext(buildLobby(), new ConfirmedState(), null);
        Player player = new Player("p1", "p1@test.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");

        assertEquals("Cupo completo y confirmado.", assertThrows(IllegalStateException.class, () -> context.postular(player, "FLEX")).getMessage());
        assertEquals("Ya están todos confirmados.", assertThrows(IllegalStateException.class, () -> context.confirmar(player)).getMessage());
        assertEquals("Debe iniciar antes de finalizar.", assertThrows(IllegalStateException.class, context::finalizar).getMessage());

        context.cancelar();
        assertEquals("Cancelado", context.getState().getStatusName());
    }

    @Test
    void playingStateShouldKeepValidationMessagesAndAllowedTransitions() {
        ScrimContext context = new ScrimContext(buildLobby(), new PlayingState(), null);
        Player player = new Player("p2", "p2@test.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");

        assertEquals("En juego.", assertThrows(IllegalStateException.class, () -> context.postular(player, "FLEX")).getMessage());
        assertEquals("En juego.", assertThrows(IllegalStateException.class, () -> context.confirmar(player)).getMessage());
        assertEquals("Ya está en juego.", assertThrows(IllegalStateException.class, context::iniciar).getMessage());
        assertEquals("No se puede cancelar una vez en juego, debe finalizarse.", assertThrows(IllegalStateException.class, context::cancelar).getMessage());

        context.finalizar();
        assertEquals("Finalizado", context.getState().getStatusName());
    }

    private Lobby buildLobby() {
        Player host = new Player("host", "host@test.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");
        setId(host);

        return new Lobby(
                LocalDateTime.now().plusMinutes(10),
                2,
                1,
                "LAS",
                "BRONCE",
                "ORO",
                80,
                "VALORANT",
                "ASCENT",
                "Buscando",
                host,
                new ArrayList<>() {{
                    add(host);
                }},
                new HashSet<>()
        );
    }

    private void setId(Player player) {
        try {
            var field = Player.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(player, UUID.randomUUID());
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }
}

