package com.pds.tp.domain.state;

import com.pds.tp.domain.entity.Lobby;
import com.pds.tp.domain.entity.Player;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScrimStateTransitionsTest {

    @Test
    void shouldFollowHappyPathTransitions() {
        Player host = new Player("host", "host@test.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");
        Player guest = new Player("guest", "guest@test.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");
        setId(host);
        setId(guest);

        Lobby lobby = new Lobby(
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
                new ArrayList<>() {{ add(host); }},
                new HashSet<>()
        );

        ScrimContext context = new ScrimContext(lobby, new SearchingState(), null);

        context.postular(guest, "FLEX");
        assertEquals("LobbyArmado", context.getState().getStatusName());

        context.confirmar(host);
        context.confirmar(guest);
        assertEquals("Confirmado", context.getState().getStatusName());

        context.iniciar();
        assertEquals("EnJuego", context.getState().getStatusName());

        context.finalizar();
        assertEquals("Finalizado", context.getState().getStatusName());
    }

    @Test
    void shouldAllowCancelBeforePlaying() {
        Player host = new Player("host2", "host2@test.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");
        setId(host);

        Lobby lobby = new Lobby(
                LocalDateTime.now().plusMinutes(10),
                2,
                1,
                "LAS",
                "BRONCE",
                "ORO",
                80,
                "VALORANT",
                "BIND",
                "Buscando",
                host,
                new ArrayList<>() {{ add(host); }},
                new HashSet<>()
        );

        ScrimContext context = new ScrimContext(lobby, new SearchingState(), null);
        context.cancelar();

        assertEquals("Cancelado", context.getState().getStatusName());
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


