package com.pds.tp.domain.state;

import org.springframework.stereotype.Component;

@Component
public class ScrimStateResolver {
    private static final String STATUS_BUSCANDO = "Buscando";
    private static final String STATUS_FINALIZADO = "Finalizado";

    public ScrimState resolve(String status) {
        if (status == null) {
            return new SearchingState();
        }

        return switch (status) {
            case STATUS_BUSCANDO -> new SearchingState();
            case "LobbyArmado" -> new CreatedLobbyState();
            case "Confirmado" -> new ConfirmedState();
            case "EnJuego" -> new PlayingState();
            case STATUS_FINALIZADO -> new FinishedState();
            case "Cancelado" -> new CanceledState();
            default -> new SearchingState();
        };
    }
}

