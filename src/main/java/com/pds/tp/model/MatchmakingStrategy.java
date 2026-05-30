package com.pds.tp.domain.strategy;

import com.pds.tp.entity.Lobby;
import com.pds.tp.entity.Player;

import java.util.List;

public interface MatchmakingStrategy {
    List<Player> seleccionar(List<Player> candidatos, Lobby lobby);
}