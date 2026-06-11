package com.pds.tp.domain.validation;

import com.pds.tp.domain.entity.Lobby;
import org.springframework.stereotype.Component;

/**
 * Default fallback validator for games without specific rules (CS2, generic).
 */
@Component("defaultGameValidator")
public class DefaultGameValidator extends GameValidator {

    @Override
    protected void validateRoles(Lobby lobby) {
        // No role constraints for generic games.
    }

    @Override
    protected void validateGameSpecificRules(Lobby lobby) {
        // No extra rules.
    }
}

