package com.pds.tp.domain.state;

import com.pds.tp.domain.entity.Player;

import java.util.Map;

public class SearchingState implements ScrimState {
    private static final Map<String, Integer> RANK_VALUES = Map.of(
            "HIERRO", 1,
            "BRONCE", 2,
            "PLATA", 3,
            "ORO", 4,
            "PLATINO", 5,
            "DIAMANTE", 6,
            "RADIANTE", 7
    );

    @Override
    public void postular(ScrimContext ctx, Player player, String role) {
        if (ctx.getLobby().getPlayers().contains(player)) {
            throw new IllegalStateException("El jugador ya está en el lobby.");
        }
        if (ctx.getLobby().getPlayers().size() >= ctx.getLobby().getMaxPlayers()) {
            throw new IllegalStateException("El lobby ya está lleno.");
        }
        if (!player.getRegion().equalsIgnoreCase(ctx.getLobby().getRegion())) {
            throw new IllegalStateException("La región del jugador no coincide con la del scrim.");
        }

        int playerRank = getRankValue(player.getVisibleRank());
        int minRank = getRankValue(ctx.getLobby().getMinRank());
        int maxRank = getRankValue(ctx.getLobby().getMaxRank());
        if (playerRank < minRank || playerRank > maxRank) {
            throw new IllegalStateException("El rango del jugador no está dentro de los límites del scrim.");
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

    private int getRankValue(String rank) {
        if (rank == null || rank.isBlank()) {
            return 0;
        }
        return RANK_VALUES.getOrDefault(rank.toUpperCase(), 0);
    }
}

