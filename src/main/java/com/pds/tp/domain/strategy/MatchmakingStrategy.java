package com.pds.tp.domain.strategy;

import com.pds.tp.domain.entity.Lobby;
import com.pds.tp.domain.entity.Player;

import java.util.List;

public interface MatchmakingStrategy {
    List<Player> seleccionar(List<Player> candidatos, Lobby lobby);
}

