package com.pds.tp.domain.state;

import com.pds.tp.domain.entity.Player;

public class CreatedLobbyState implements ScrimState {
    @Override
    public void postular(ScrimContext ctx, Player player, String role) {
        throw StateErrorStyle.invalidTransition("El lobby ya está completo.");
    }

    @Override
    public void confirmar(ScrimContext ctx, Player player) {
        if (!ctx.getLobby().getPlayers().contains(player)) {
            throw StateErrorStyle.invalidTransition("El jugador no pertenece a este lobby.");
        }
        boolean isNewConfirmation = ctx.getLobby().getConfirmedPlayerUsernames().add(player.getUsername());
        if (!isNewConfirmation) {
            throw StateErrorStyle.invalidTransition("El jugador ya confirmó su participación.");
        }

        if (ctx.getLobby().getConfirmedPlayerUsernames().size() == ctx.getLobby().getMaxPlayers()) {
            ctx.setState(new ConfirmedState());
        }
    }

    @Override
    public void iniciar(ScrimContext ctx) {
        throw StateErrorStyle.invalidTransition("Faltan confirmaciones de jugadores.");
    }

    @Override
    public void finalizar(ScrimContext ctx) {
        throw StateErrorStyle.invalidTransition("El scrim aún no ha comenzado.");
    }

    @Override
    public void cancelar(ScrimContext ctx) {
        ctx.setState(new CanceledState());
    }

    @Override
    public String getStatusName() {
        return "LobbyArmado";
    }
}
