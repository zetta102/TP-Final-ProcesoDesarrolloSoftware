package com.pds.tp.domain.state;

import com.pds.tp.domain.entity.Player;

public class ConfirmedState implements ScrimState {
    @Override
    public void postular(ScrimContext ctx, Player player, String role) {
        throw new IllegalStateException("Cupo completo y confirmado.");
    }

    @Override
    public void confirmar(ScrimContext ctx, Player player) {
        throw new IllegalStateException("Ya están todos confirmados.");
    }

    @Override
    public void iniciar(ScrimContext ctx) {
        ctx.setState(new PlayingState());
    }

    @Override
    public void finalizar(ScrimContext ctx) {
        throw new IllegalStateException("Debe iniciar antes de finalizar.");
    }

    @Override
    public void cancelar(ScrimContext ctx) {
        ctx.setState(new CanceledState());
    }

    @Override
    public String getStatusName() {
        return "Confirmado";
    }
}

