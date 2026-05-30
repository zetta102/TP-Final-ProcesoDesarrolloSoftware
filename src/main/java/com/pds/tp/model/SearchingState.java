package com.pds.tp.domain.state;

import com.pds.tp.entity.Player;
import com.pds.tp.model.ScrimContext;
import com.pds.tp.model.ScrimState;

public class SearchingState implements ScrimState {

    @Override
    public void postular(ScrimContext ctx, Player player, String role) {
        if (ctx.getLobby().getPlayers().contains(player)) {
            throw new IllegalStateException("Player is already in the lobby.");
        }

        ctx.getLobby().getPlayers().add(player);

        // Transition rule: If full, move to LobbyArmado
        if (ctx.getLobby().getPlayers().size() >= ctx.getLobby().getMaxPlayers()) {
            ctx.setState(new LobbyArmadoState());
        }
    }

    @Override
    public void confirmar(ScrimContext ctx, Player player) {
        throw new IllegalStateException("Cannot confirm until the lobby is full (Lobby Armado).");
    }

    @Override
    public void iniciar(ScrimContext ctx) {
        throw new IllegalStateException("Cannot start a scrim that is still looking for players.");
    }

    @Override
    public void finalizar(ScrimContext ctx) {
        throw new IllegalStateException("Cannot finish a scrim that hasn't started.");
    }

    @Override
    public void cancelar(ScrimContext ctx) {
        ctx.setState(new CanceladoState());
    }

    @Override
    public String getStatusName() {
        return "Buscando";
    }
}