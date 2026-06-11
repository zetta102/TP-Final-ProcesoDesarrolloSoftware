package com.pds.tp.application.dto;

public record OAuthCallbackData(
        String provider,
        String oauthToken
) {
}

