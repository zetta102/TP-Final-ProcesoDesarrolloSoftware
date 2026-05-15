package com.pds.tp.controller;

import com.pds.tp.entity.Player;
import com.pds.tp.model.LoginData;
import com.pds.tp.model.PlayerData;
import com.pds.tp.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("v1/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Player register(@RequestBody PlayerData playerData) {
        return authService.register(playerData);
    }

    @PostMapping("/login")
    public boolean login(@RequestBody LoginData loginData) {
        return authService.authenticate(loginData.username(), loginData.password());
    }
}
