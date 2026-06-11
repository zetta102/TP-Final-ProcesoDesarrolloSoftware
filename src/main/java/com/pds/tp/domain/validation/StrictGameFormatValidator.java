package com.pds.tp.domain.validation;

import com.pds.tp.domain.entity.Lobby;
import org.springframework.stereotype.Component;

/**
 * Validates composition: strict 5v5 format.
 */
@Component("strictGameValidator")
public class StrictGameFormatValidator extends GameValidator {

    @Override
    protected void validateRoles(Lobby lobby) {
        // Enforces strict 5v5 format.
        if (lobby.getMaxPlayers() != 10) {
            throw new IllegalStateException("Este formato solo soporta 5v5 (10 jugadores).");
        }
    }

    @Override
    protected void validateGameSpecificRules(Lobby lobby) {
        // No additional game-specific rules for strict prototype.
    }
}

