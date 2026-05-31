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

class TerminalScrimStateTest {

    @Test
    void canceledStateShouldRejectEveryOperation() {
        ScrimContext context = new ScrimContext(buildLobby(), new CanceledState(), null);
        Player player = new Player("p1", "p1@test.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");

        assertEquals("Scrim is canceled.", assertThrows(IllegalStateException.class, () -> context.postular(player, "FLEX")).getMessage());
        assertEquals("Scrim is canceled.", assertThrows(IllegalStateException.class, () -> context.confirmar(player)).getMessage());
        assertEquals("Scrim is canceled.", assertThrows(IllegalStateException.class, context::iniciar).getMessage());
        assertEquals("Scrim is canceled.", assertThrows(IllegalStateException.class, context::finalizar).getMessage());
        assertEquals("Scrim is already canceled.", assertThrows(IllegalStateException.class, context::cancelar).getMessage());
    }

    @Test
    void finishedStateShouldRejectEveryOperation() {
        ScrimContext context = new ScrimContext(buildLobby(), new FinishedState(), null);
        Player player = new Player("p2", "p2@test.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");

        assertEquals("Scrim is already finished.", assertThrows(IllegalStateException.class, () -> context.postular(player, "FLEX")).getMessage());
        assertEquals("Scrim is already finished.", assertThrows(IllegalStateException.class, () -> context.confirmar(player)).getMessage());
        assertEquals("Scrim is already finished.", assertThrows(IllegalStateException.class, context::iniciar).getMessage());
        assertEquals("Scrim is already finished.", assertThrows(IllegalStateException.class, context::finalizar).getMessage());
        assertEquals("Scrim is already finished.", assertThrows(IllegalStateException.class, context::cancelar).getMessage());
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

