package com.pds.tp.service;

import com.pds.tp.entity.Player;
import com.pds.tp.model.PlayerData;
import com.pds.tp.repository.PlayerRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final PlayerRepository playerRepository;

    public AuthService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
    public Player register(PlayerData playerData) {
        Player player = new Player(playerData.playerName(), playerData.password(), playerData.preferredRole(), playerData.region(), playerData.platform(), playerData.availability());
        return playerRepository.save(player);
    }

        public boolean authenticate(String username, String password) {
            // Aquí puedes implementar la lógica de autenticación, por ejemplo, consultando la base de datos
            // para verificar si el usuario existe y si la contraseña es correcta.
            return playerRepository.existsByUsernameAndPassword(username, password);
        }
}
