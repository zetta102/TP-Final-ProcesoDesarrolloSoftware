package com.pds.tp.model;

import com.pds.tp.entity.Player;

public interface ScrimState {
    void postular(ScrimContext ctx, Player player, String role);

    void confirmar(ScrimContext ctx, Player player);

    void iniciar(ScrimContext ctx);

    void finalizar(ScrimContext ctx);

    void cancelar(ScrimContext ctx);

    String getStatusName();
}