package com.pds.tp.domain.validation;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Resolves the appropriate GameValidator (Template Method) based on the game name.
 */
@Component
public class GameValidatorFactory {

    private final Map<String, GameValidator> validators;
    private final GameValidator defaultValidator;

    public GameValidatorFactory(@Qualifier("flexibleGameValidator") GameValidator flexibleGameValidator,
                                @Qualifier("strictGameValidator") GameValidator strictGameValidator,
                                @Qualifier("defaultGameValidator") GameValidator defaultGameValidator) {
        this.defaultValidator = defaultGameValidator;
        this.validators = Map.of(
                "FLEXIBLE", flexibleGameValidator,
                "STRICT", strictGameValidator
        );
    }

    public GameValidator resolve(String gameName) {
        if (gameName == null || gameName.isBlank()) {
            return defaultValidator;
        }
        return validators.getOrDefault(gameName.trim().toUpperCase(), defaultValidator);
    }
}

