package com.pds.tp.domain.validation;

import com.pds.tp.domain.entity.Lobby;
import org.springframework.stereotype.Component;

/**
 * Validates flexible composition: 5v5 format, max latency constraint.
 */
@Component("flexibleGameValidator")
public class FlexibleGameFormatValidator extends GameValidator {

    private static final int VALORANT_MAX_PING = 150;

    @Override
    protected void validateRoles(Lobby lobby) {
        // Supports 5v5 (10 players) or 1v1 (2 players) in custom scrims.
        int maxPlayers = lobby.getMaxPlayers();
        if (maxPlayers != 10 && maxPlayers != 2) {
            throw new IllegalStateException("Solo soporta formato 5v5 (10 jugadores) o 1v1 (2 jugadores).");
        }
    }

    @Override
    protected void validateGameSpecificRules(Lobby lobby) {
        if (lobby.getMaxPing() > 0 && lobby.getMaxPing() > VALORANT_MAX_PING) {
            throw new IllegalStateException("Requiere una latencia máxima de " + VALORANT_MAX_PING + "ms para scrims competitivos.");
        }
    }
}

