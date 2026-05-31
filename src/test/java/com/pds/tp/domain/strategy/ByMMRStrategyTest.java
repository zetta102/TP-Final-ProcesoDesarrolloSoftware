package com.pds.tp.domain.strategy;

import com.pds.tp.domain.entity.Lobby;
import com.pds.tp.domain.entity.Player;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ByMMRStrategyTest {

    @Test
    void shouldSelectOnlyPlayersWithinRankRange() {
        Player host = new Player("host", "host@mmr.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");
        setId(host);
        Lobby lobby = new Lobby(
                LocalDateTime.now().plusHours(1),
                3,
                1,
                "LAS",
                "PLATA",
                "ORO",
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

        Player bronze = new Player("bronze", "b@x.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");
        Player silver = new Player("silver", "s@x.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");
        Player gold = new Player("gold", "g@x.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");
        Player radiant = new Player("radiant", "r@x.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");
        setId(bronze);
        setId(silver);
        setId(gold);
        setId(radiant);

        setVisibleRank(bronze, "BRONCE");
        setVisibleRank(silver, "PLATA");
        setVisibleRank(gold, "ORO");
        setVisibleRank(radiant, "RADIANTE");

        List<Player> selected = new ByMMRStrategy().seleccionar(List.of(bronze, silver, gold, radiant), lobby);

        assertEquals(2, selected.size());
        assertTrue(selected.contains(silver));
        assertTrue(selected.contains(gold));
    }

    @Test
    void shouldProcess500CandidatesUnderTwoSeconds() {
        Player host = new Player("host2", "host2@mmr.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");
        setId(host);
        Lobby lobby = new Lobby(
                LocalDateTime.now().plusHours(1),
                10,
                5,
                "LAS",
                "BRONCE",
                "RADIANTE",
                80,
                "VALORANT",
                "LOTUS",
                "Buscando",
                host,
                new ArrayList<>() {{
                    add(host);
                }},
                new HashSet<>()
        );

        List<Player> candidates = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            Player player = new Player("p" + i, "p" + i + "@x.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");
            setId(player);
            setVisibleRank(player, i % 2 == 0 ? "ORO" : "PLATA");
            candidates.add(player);
        }

        long start = System.nanoTime();
        new ByMMRStrategy().seleccionar(candidates, lobby);
        long elapsedMillis = (System.nanoTime() - start) / 1_000_000;

        assertTrue(elapsedMillis < 2000, "Matchmaking exceeded 2 seconds");
    }

    private void setVisibleRank(Player player, String rank) {
        try {
            var field = Player.class.getDeclaredField("visibleRank");
            field.setAccessible(true);
            field.set(player, rank);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException(ex);
        }
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


