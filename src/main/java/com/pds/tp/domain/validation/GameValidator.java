package com.pds.tp.domain.validation;

import com.pds.tp.domain.entity.Lobby;

/**
 * Template Method for validating team composition per game.
 * Subclasses define game-specific rules (role requirements, team size constraints).
 */
public abstract class GameValidator {

    /**
     * Template method: validates a lobby composition in a fixed sequence.
     */
    public final void validate(Lobby lobby) {
        validateTeamSize(lobby);
        validateRoles(lobby);
        validateGameSpecificRules(lobby);
    }

    protected void validateTeamSize(Lobby lobby) {
        if (lobby.getMaxPlayers() < 2) {
            throw new IllegalStateException("Un scrim requiere al menos 2 jugadores.");
        }
    }

    protected abstract void validateRoles(Lobby lobby);

    protected abstract void validateGameSpecificRules(Lobby lobby);
}

