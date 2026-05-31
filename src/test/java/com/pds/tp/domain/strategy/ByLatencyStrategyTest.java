package com.pds.tp.domain.strategy;

import com.pds.tp.domain.entity.Lobby;
import com.pds.tp.domain.entity.Player;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ByLatencyStrategyTest {

    @Test
    void shouldRejectPlayersAboveLobbyLatencyThreshold() {
        Player host = new Player("host", "host@lat.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");
        Player fast = new Player("fast", "fast@lat.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");
        Player slow = new Player("slow", "slow@lat.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");
        Player otherRegion = new Player("other", "other@lat.com", "pwd", "FLEX", "LAN", "PC", "NOCHE");

        setId(host);
        setId(fast);
        setId(slow);
        setId(otherRegion);

        fast.setAveragePingMs(40);
        slow.setAveragePingMs(120);
        otherRegion.setAveragePingMs(20);

        Lobby lobby = new Lobby(
                LocalDateTime.now().plusHours(1),
                4,
                1,
                "LAS",
                "BRONCE",
                "RADIANTE",
                80,
                "VALORANT",
                "HAVEN",
                "Buscando",
                host,
                new ArrayList<>() {{
                    add(host);
                }},
                new HashSet<>()
        );

        var selected = new ByLatencyStrategy().seleccionar(
                java.util.List.of(fast, slow, otherRegion),
                lobby
        );

        assertEquals(1, selected.size());
        assertTrue(selected.contains(fast));
        assertFalse(selected.contains(slow));
        assertFalse(selected.contains(otherRegion));
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


