package com.pds.tp.domain.state;

import com.pds.tp.domain.entity.Lobby;
import com.pds.tp.domain.entity.Player;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchingStateTest {

    @Test
    void shouldRejectPostulationWhenPlayerRankIsOutsideConfiguredRange() {
        Player host = new Player("host", "host@state.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");
        Player silver = new Player("silver", "silver@state.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");
        setId(host);
        setId(silver);

        host.setVisibleRank("ORO");
        silver.setVisibleRank("PLATA");

        Lobby lobby = new Lobby(
                LocalDateTime.now().plusMinutes(30),
                2,
                1,
                "LAS",
                "ORO",
                "PLATINO",
                80,
                "VALORANT",
                "BIND",
                "Buscando",
                host,
                new ArrayList<>() {{
                    add(host);
                }},
                new HashSet<>()
        );

        ScrimContext context = new ScrimContext(lobby, new SearchingState(), null);

        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> context.postular(silver, "FLEX"));

        assertTrue(thrown.getMessage().contains("rango"));
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

