package com.pds.tp.domain.state;

import com.pds.tp.domain.event.ScrimStateChangedEvent;
import com.pds.tp.domain.entity.Lobby;
import com.pds.tp.domain.entity.Player;
import lombok.Getter;
import org.springframework.context.ApplicationEventPublisher;

@Getter
public class ScrimContext {
    private final Lobby lobby;
    private final ApplicationEventPublisher eventPublisher;
    private ScrimState state;

    public ScrimContext(Lobby lobby, ScrimState initialState, ApplicationEventPublisher eventPublisher) {
        this.lobby = lobby;
        this.state = initialState;
        this.eventPublisher = eventPublisher;
    }

    public void setState(ScrimState state) {
        this.state = state;
        this.lobby.setStatus(state.getStatusName());
        // Automatically publish domain event whenever state changes
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new ScrimStateChangedEvent(this, lobby.getId(), state.getStatusName()));
        }
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

    public void finalizar() {
        state.finalizar(this);
    }

    public void cancelar() {
        state.cancelar(this);
    }
}

