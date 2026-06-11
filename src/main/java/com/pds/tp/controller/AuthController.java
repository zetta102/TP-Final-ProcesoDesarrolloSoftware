package com.pds.tp.controller;

import com.pds.tp.application.dto.LoginData;
import com.pds.tp.application.dto.PlayerData;
import com.pds.tp.application.service.AuthService;
import com.pds.tp.domain.entity.Player;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping( "/v1/api/auth")
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
        boolean authenticated = authService.authenticate(loginData.identifier(), loginData.password());
        if (authenticated) {
            return ResponseEntity.ok(Map.of(MESSAGE_KEY, "Autenticación exitosa", "token", "Bearer eyJhbG..."));
        } else {
            return ResponseEntity.status(401).body(Map.of(MESSAGE_KEY, "Credenciales inválidas o email no verificado."));
        }
    }

    @PostMapping("/{username}/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@PathVariable String username) {
        String message = authService.verifyEmail(username);
        return ResponseEntity.ok(Map.of(MESSAGE_KEY, message));
    }
}
