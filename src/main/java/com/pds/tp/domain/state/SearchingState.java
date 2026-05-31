package com.pds.tp.domain.state;

import com.pds.tp.domain.entity.Player;

public class SearchingState implements ScrimState {
    @Override
    public void postular(ScrimContext ctx, Player player, String role) {
        if (ctx.getLobby().getPlayers().contains(player)) {
            throw new IllegalStateException("El jugador ya está en el lobby.");
        }
        if (ctx.getLobby().getPlayers().size() >= ctx.getLobby().getMaxPlayers()) {
            throw new IllegalStateException("El lobby ya está lleno.");
        }

        ctx.getLobby().getPlayers().add(player);

        if (ctx.getLobby().getPlayers().size() == ctx.getLobby().getMaxPlayers()) {
            ctx.setState(new CreatedLobbyState());
        }
    }

    @Override
    public void confirmar(ScrimContext ctx, Player player) {
        throw new IllegalStateException("No se puede confirmar en estado Buscando.");
    }

    @Override
    public void iniciar(ScrimContext ctx) {
        throw new IllegalStateException("No se puede iniciar en estado Buscando.");
    }

    @Override
    public void finalizar(ScrimContext ctx) {
        throw new IllegalStateException("No se puede finalizar en estado Buscando.");
    }

    @Override
    public void cancelar(ScrimContext ctx) {
        ctx.setState(new CanceledState());
    }

    @Override
    public String getStatusName() {
        return "Buscando";
    }
}

