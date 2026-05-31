package com.pds.tp.domain.strategy;

import com.pds.tp.domain.entity.Lobby;
import com.pds.tp.domain.entity.Player;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Lightweight benchmark guard for matchmaking performance requirement.
 */
class ByMMRStrategyBenchmarkTest {

    @Test
    void shouldSelectPlayersBelowTwoSecondsFor500Candidates() {
        ByMMRStrategy strategy = new ByMMRStrategy();
        Lobby lobby = createTestLobby();
        List<Player> candidates = generateCandidates(500);

        long start = System.nanoTime();
        List<Player> selected = strategy.seleccionar(candidates, lobby);
        long elapsedMillis = (System.nanoTime() - start) / 1_000_000;

        assertTrue(elapsedMillis < 2000, "Emparejamiento tardo " + elapsedMillis + "ms, esperado < 2000ms");
        assertTrue(!selected.isEmpty(), "Debe seleccionar al menos un candidato");
    }

    private Lobby createTestLobby() {
        Player host = new Player("host-bench", "host-bench@test.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");
        setId(host);
        setVisibleRank(host, "PLATA");

        List<Player> players = new ArrayList<>();
        players.add(host);

        return new Lobby(
                LocalDateTime.now().plusHours(1),
                10,
                1,
                "LAS",
                "BRONCE",
                "DIAMANTE",
                80,
                "VALORANT",
                "HAVEN",
                "Buscando",
                host,
                players,
                new HashSet<>()
        );
    }

    private List<Player> generateCandidates(int count) {
        String[] ranks = {"BRONCE", "PLATA", "ORO", "PLATINO", "DIAMANTE"};
        Random random = new Random(42);

        List<Player> players = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            Player player = new Player(
                    "player" + i,
                    "player" + i + "@test.com",
                    "pwd",
                    "FLEX",
                    "LAS",
                    "PC",
                    "NOCHE"
            );
            setId(player);
            setVisibleRank(player, ranks[random.nextInt(ranks.length)]);
            players.add(player);
        }
        return players;
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

