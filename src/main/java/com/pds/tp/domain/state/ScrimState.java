package com.pds.tp.domain.state;

import com.pds.tp.domain.entity.Player;

public interface ScrimState {
    void postular(ScrimContext ctx, Player player, String role);

    void confirmar(ScrimContext ctx, Player player);

    void iniciar(ScrimContext ctx);

    void finalizar(ScrimContext ctx);

    void cancelar(ScrimContext ctx);

    String getStatusName();
}

