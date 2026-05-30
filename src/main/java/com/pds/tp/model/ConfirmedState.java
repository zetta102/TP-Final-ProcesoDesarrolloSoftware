package com.pds.tp.model;

import com.pds.tp.entity.Player;

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
        ctx.setState(new EnJuegoState());
    }

    @Override
    public void finalizar(ScrimContext ctx) {
        throw new IllegalStateException("Debe iniciar antes de finalizar.");
    }

    @Override
    public void cancelar(ScrimContext ctx) {
        ctx.setState(new CanceladoState());
    }

    @Override
    public String getStatusName() {
        return "Confirmado";
    }
}