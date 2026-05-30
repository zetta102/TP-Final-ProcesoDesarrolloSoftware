package com.pds.tp.model;

import com.pds.tp.entity.Lobby;
import com.pds.tp.entity.Player;
import lombok.Getter;

@Getter
public class ScrimContext {
    private ScrimState state;
    private final Lobby lobby;

    public ScrimContext(Lobby lobby, ScrimState initialState) {
        this.lobby = lobby;
        this.state = initialState;
    }

    public void setState(ScrimState state) {
        this.state = state;
        this.lobby.setStatus(state.getStatusName()); // Sync with DB entity
    }

    public void postular(Player player, String role) {
        state.postular(this, player, role);
    }

    public void confirmar(Player player) {
        state.confirmar(this, player);
    }

    public void iniciar() {
        state.iniciar(this);
    }

    public void cancelar() {
        state.cancelar(this);
    }
}