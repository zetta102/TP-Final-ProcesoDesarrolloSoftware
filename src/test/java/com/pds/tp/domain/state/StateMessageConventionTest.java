package com.pds.tp.domain.state;

import com.pds.tp.domain.entity.Lobby;
import com.pds.tp.domain.entity.Player;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StateMessageConventionTest {

    @Test
    void shouldRejectMisformattedStateMessageDefinitions() {
        assertThrows(IllegalArgumentException.class, () -> StateErrorStyle.invalidTransition("Sin punto final"));
    }

    @Test
    void shouldKeepTrailingDotConventionAcrossStateErrors() {
        Player host = new Player("host", "host@test.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");
        Player guest = new Player("guest", "guest@test.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");
        setId(host);
        setId(guest);

        Lobby baseLobby = new Lobby(
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

        assertMessageStyle(() -> new ScrimContext(baseLobby, new SearchingState(), null).iniciar());
        assertMessageStyle(() -> new ScrimContext(baseLobby, new CreatedLobbyState(), null).finalizar());
        assertMessageStyle(() -> new ScrimContext(baseLobby, new ConfirmedState(), null).confirmar(guest));
        assertMessageStyle(() -> new ScrimContext(baseLobby, new PlayingState(), null).cancelar());
        assertMessageStyle(() -> new ScrimContext(baseLobby, new FinishedState(), null).iniciar());
        assertMessageStyle(() -> new ScrimContext(baseLobby, new CanceledState(), null).postular(guest, "FLEX"));
    }

    private void assertMessageStyle(ThrowingOperation operation) {
        IllegalStateException exception = assertThrows(IllegalStateException.class, operation::run);
        assertFalse(exception.getMessage().isBlank());
        assertTrue(exception.getMessage().endsWith("."));
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

    @FunctionalInterface
    private interface ThrowingOperation {
        void run();
    }
}


