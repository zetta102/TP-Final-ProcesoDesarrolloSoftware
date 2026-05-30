package com.pds.tp.model;

import com.pds.tp.entity.Lobby;
import com.pds.tp.entity.Player;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Primary
public class ByMMRStrategy implements com.pds.tp.domain.strategy.MatchmakingStrategy {

    private final Map<String, Integer> rankValues = Map.of(
            "HIERRO", 1, "BRONCE", 2, "PLATA", 3, "ORO", 4,
            "PLATINO", 5, "DIAMANTE", 6, "RADIANTE", 7
    );

    @Override
    public List<Player> seleccionar(List<Player> candidatos, Lobby lobby) {
        int minLobbyRank = getRankValue(lobby.getMinRank());
        int maxLobbyRank = getRankValue(lobby.getMaxRank());

        return candidatos.stream()
                .filter(p -> {
                    int playerRank = getRankValue(p.getVisibleRank());
                    return playerRank >= minLobbyRank && playerRank <= maxLobbyRank;
                })
                .limit(lobby.getMaxPlayers() - lobby.getPlayers().size())
                .collect(Collectors.toList());
    }

    private int getRankValue(String rank) {
        if (rank == null) return 0;
        return rankValues.getOrDefault(rank.toUpperCase(), 0);
    }
}