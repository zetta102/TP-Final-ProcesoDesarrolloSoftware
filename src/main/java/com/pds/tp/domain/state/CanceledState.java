package com.pds.tp.domain.state;

import com.pds.tp.domain.entity.Player;

public class CanceledState implements ScrimState {
    @Override
    public void postular(ScrimContext ctx, Player p, String r) {
        throw new IllegalStateException("Cancelado.");
    }

    @Override
    public void confirmar(ScrimContext ctx, Player p) {
        throw new IllegalStateException("Cancelado.");
    }

    @Override
    public void iniciar(ScrimContext ctx) {
        throw new IllegalStateException("Cancelado.");
    }

    @Override
    public void finalizar(ScrimContext ctx) {
        throw new IllegalStateException("Cancelado.");
    }

    @Override
    public void cancelar(ScrimContext ctx) {
        throw new IllegalStateException("Ya cancelado.");
    }

    @Override
    public String getStatusName() {
        return "Cancelado";
    }
}

