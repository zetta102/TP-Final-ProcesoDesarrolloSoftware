package com.pds.tp.domain.state;

import com.pds.tp.domain.entity.Player;
import com.pds.tp.domain.shared.RankScale;

public class SearchingState implements ScrimState {
    @Override
    public void postular(ScrimContext ctx, Player player, String role) {
        if (ctx.getLobby().getPlayers().contains(player)) {
            throw StateErrorStyle.invalidTransition("El jugador ya está en el lobby.");
        }
        if (ctx.getLobby().getPlayers().size() >= ctx.getLobby().getMaxPlayers()) {
            throw StateErrorStyle.invalidTransition("El lobby ya esta lleno.");
        }
        if (!player.getRegion().equalsIgnoreCase(ctx.getLobby().getRegion())) {
            throw StateErrorStyle.invalidTransition("La región del jugador no coincide con la del scrim.");
        }

        int playerRank = RankScale.toValue(player.getVisibleRank());
        int minRank = RankScale.toValue(ctx.getLobby().getMinRank());
        int maxRank = RankScale.toValue(ctx.getLobby().getMaxRank());
        if (playerRank < minRank || playerRank > maxRank) {
            throw StateErrorStyle.invalidTransition("El rango del jugador no está dentro de los límites del scrim.");
        }

        ctx.getLobby().getPlayers().add(player);

        if (ctx.getLobby().getPlayers().size() == ctx.getLobby().getMaxPlayers()) {
            ctx.setState(new CreatedLobbyState());
        }
    }

    @Override
    public void confirmar(ScrimContext ctx, Player player) {
        throw StateErrorStyle.invalidTransition("No se puede confirmar en estado Buscando.");
    }

    @Override
    public void iniciar(ScrimContext ctx) {
        throw StateErrorStyle.invalidTransition("No se puede iniciar en estado Buscando.");
    }

    @Override
    public void finalizar(ScrimContext ctx) {
        throw StateErrorStyle.invalidTransition("No se puede finalizar en estado Buscando.");
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

