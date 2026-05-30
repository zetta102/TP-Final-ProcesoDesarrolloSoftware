package com.pds.tp.model;

import com.pds.tp.entity.Lobby;
import com.pds.tp.entity.Player;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ByLatencyStrategy implements com.pds.tp.domain.strategy.MatchmakingStrategy {
    @Override
    public List<Player> seleccionar(List<Player> candidatos, Lobby lobby) {
        return candidatos.stream()
                .filter(p -> p.getRegion().equalsIgnoreCase(lobby.getRegion()))
                // In a real app, ping would be dynamically evaluated here
                .limit(lobby.getMaxPlayers() - lobby.getPlayers().size())
                .collect(Collectors.toList());
    }
}