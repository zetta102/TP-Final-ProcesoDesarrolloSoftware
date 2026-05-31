package com.pds.tp.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class ScrimStateChangedEvent extends ApplicationEvent {
    private final UUID lobbyId;
    private final String nuevoEstado;

    public ScrimStateChangedEvent(Object source, UUID lobbyId, String nuevoEstado) {
        super(source);
        this.lobbyId = lobbyId;
        this.nuevoEstado = nuevoEstado;
    }

}

