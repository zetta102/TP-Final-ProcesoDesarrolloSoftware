package com.pds.tp.model;

import com.pds.tp.entity.Lobby;
import com.pds.tp.entity.Player;
import java.util.List;

public interface MatchmakingStrategy {
    List<Player> seleccionar(List<Player> candidatos, Lobby lobby);
}