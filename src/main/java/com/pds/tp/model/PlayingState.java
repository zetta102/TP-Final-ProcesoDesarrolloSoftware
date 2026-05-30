package com.pds.tp.model;

import com.pds.tp.entity.Player;

public class PlayingState implements ScrimState {
    @Override
    public void postular(ScrimContext ctx, Player p, String r) {
        throw new IllegalStateException("En juego.");
    }

    @Override
    public void confirmar(ScrimContext ctx, Player p) {
        throw new IllegalStateException("En juego.");
    }

    @Override
    public void iniciar(ScrimContext ctx) {
        throw new IllegalStateException("Ya está en juego.");
    }

    @Override
    public void finalizar(ScrimContext ctx) {
        ctx.setState(new FinalizadoState());
    }

    @Override
    public void cancelar(ScrimContext ctx) {
        throw new IllegalStateException("No se puede cancelar una vez en juego, debe finalizarse.");
    }

    @Override
    public String getStatusName() {
        return "EnJuego";
    }
}