package com.pds.tp.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Mock OAuth provider that simulates the callback from Steam/Riot/Discord.
 * In production, this would call the actual OAuth2 provider endpoints.
 */
@Slf4j
@Component
public class MockOAuthProvider {

    /**
     * Simulates validating an OAuth token and returning user profile data.
     *
     * @param provider   The OAuth provider (steam, riot, discord)
     * @param oauthToken The token received from the OAuth flow
     * @return A map with user profile data (username, email, avatarUrl)
     */
    public Map<String, String> validateAndFetchProfile(String provider, String oauthToken) {
        log.info("[MOCK OAUTH] Validando token con provider '{}': {}", provider, oauthToken);

        // Simulate different provider responses
        String simulatedUsername = provider + "_user_" + oauthToken.hashCode();
        String simulatedEmail = simulatedUsername + "@" + provider + ".mock";

        log.info("[MOCK OAUTH] Provider '{}' devolvió perfil: username={}, email={}",
                provider, simulatedUsername, simulatedEmail);

        return Map.of(
                "username", simulatedUsername,
                "email", simulatedEmail,
                "provider", provider,
                "verified", "true"
        );
    }
}

