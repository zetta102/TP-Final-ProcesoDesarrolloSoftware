package com.pds.tp.domain.state;

import com.pds.tp.domain.entity.Player;

public class FinishedState implements ScrimState {
    @Override
    public void postular(ScrimContext ctx, Player p, String r) {
        throw new IllegalStateException("Finalizado.");
    }

    @Override
    public void confirmar(ScrimContext ctx, Player p) {
        throw new IllegalStateException("Finalizado.");
    }

    @Override
    public void iniciar(ScrimContext ctx) {
        throw new IllegalStateException("Finalizado.");
    }

    @Override
    public void finalizar(ScrimContext ctx) {
        throw new IllegalStateException("Ya finalizado.");
    }

    @Override
    public void cancelar(ScrimContext ctx) {
        throw new IllegalStateException("Finalizado.");
    }

    @Override
    public String getStatusName() {
        return "Finalizado";
    }
}

