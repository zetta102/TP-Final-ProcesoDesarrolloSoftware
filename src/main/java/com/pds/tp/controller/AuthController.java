package com.pds.tp.controller;

import com.pds.tp.application.dto.LoginData;
import com.pds.tp.application.dto.OAuthCallbackData;
import com.pds.tp.application.dto.PlayerData;
import com.pds.tp.application.service.AuthService;
import com.pds.tp.domain.entity.Player;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/api/auth")
public class AuthController {
    private static final String MESSAGE_KEY = "mensaje";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Player> register(@RequestBody PlayerData playerData) {
        return ResponseEntity.status(201).body(authService.register(playerData));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginData loginData) {
        String token = authService.authenticate(loginData.identifier(), loginData.password());
        if (token != null) {
            return ResponseEntity.ok(Map.of(MESSAGE_KEY, "Autenticación exitosa", "token", "Bearer " + token));
        } else {
            return ResponseEntity.status(401).body(Map.of(MESSAGE_KEY, "Credenciales inválidas, email no verificado o usuario baneado."));
        }
    }

    @PostMapping("/oauth/callback")
    public ResponseEntity<Player> oauthCallback(@RequestBody OAuthCallbackData callbackData) {
        return ResponseEntity.status(201).body(authService.registerViaOAuth(callbackData));
    }

    @PostMapping("/{username}/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(
            @PathVariable String username,
            @RequestParam(required = false) String token) {
        String message;
        if (token != null && !token.isBlank()) {
            message = authService.verifyEmailWithToken(username, token);
        } else {
            message = authService.verifyEmail(username);
        }
        return ResponseEntity.ok(Map.of(MESSAGE_KEY, message));
    }
}
