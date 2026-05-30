package com.pds.tp.domain.state;

import com.pds.tp.entity.Player;
import com.pds.tp.model.ScrimContext;
import com.pds.tp.model.ScrimState;

public class CreatedLobbyState implements ScrimState {
    @Override
    public void postular(ScrimContext ctx, Player player, String role) {
        throw new IllegalStateException("El cupo ya está completo.");
    }

    @Override
    public void confirmar(ScrimContext ctx, Player player) {
        if (!ctx.getLobby().getPlayers().contains(player)) {
            throw new IllegalStateException("El jugador no pertenece al lobby.");
        }
        ctx.getConfirmedPlayers().add(player);

        if (ctx.getConfirmedPlayers().size() == ctx.getLobby().getMaxPlayers()) {
            ctx.setState(new ConfirmadoState());
        }
    }

    @Override
    public void iniciar(ScrimContext ctx) {
        throw new IllegalStateException("Faltan confirmaciones.");
    }

    @Override
    public void finalizar(ScrimContext ctx) {
        throw new IllegalStateException("Aún no ha iniciado.");
    }

    @Override
    public void cancelar(ScrimContext ctx) {
        ctx.setState(new CanceladoState());
    }

    @Override
    public String getStatusName() {
        return "LobbyArmado";
    }
}