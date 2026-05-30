package com.pds.tp.controller;

import com.pds.tp.entity.Player;
import com.pds.tp.model.LoginData;
import com.pds.tp.model.PlayerData;
import com.pds.tp.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Player> register(@RequestBody PlayerData playerData) {
        return ResponseEntity.status(201).body(authService.register(playerData));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginData loginData) {
        boolean authenticated = authService.authenticate(loginData.username(), loginData.password());
        if (authenticated) {
            // En un caso real se devolvería un JWT, aquí simulamos el formato.
            return ResponseEntity.ok(Map.of("mensaje", "Autenticación exitosa", "token", "Bearer eyJhbG..."));
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }
    }
}