package com.pds.tp.model;

import com.pds.tp.entity.Lobby;
import com.pds.tp.entity.Player;
import com.pds.tp.model.MatchmakingStrategy;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ByMMRStrategy implements MatchmakingStrategy {

    @Override
    public List<Player> seleccionar(List<Player> candidatos, Lobby lobby) {
        // Example: Filter candidates strictly by the Lobby's acceptable rank range
        // Note: You would convert "Hierro", "Oro", etc., to numeric MMR values here
        return candidatos.stream()
                .filter(p -> isRankWithinRange(p.getVisibleRank(), lobby.getMinRank(), lobby.getMaxRank()))
                .limit(lobby.getMaxPlayers())
                .collect(Collectors.toList());
    }

    private boolean isRankWithinRange(String playerRank, String minRank, String maxRank) {
        // Implement rank comparison logic here
        return true;
    }
}