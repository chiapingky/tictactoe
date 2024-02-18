package org.chiapingky.tictactoe.account;

import org.chiapingky.tictactoe.auth.JwtService;
import org.chiapingky.tictactoe.user.Player;
import org.chiapingky.tictactoe.user.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AccountService {
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    public String register(String username, String password) {
        Player exist = playerRepository.findById(username).orElse(null);
        if (exist != null)
            return "";
        Player newPlayer = new Player(username, passwordEncoder.encode(password));
        playerRepository.save(newPlayer);
        return jwtService.generateToken(Map.of(), newPlayer.getUsername());
    }

    public String authenticate(String username, String password) {
        Player player = playerRepository.findById(username).orElse(null);
        if (player != null && passwordEncoder.matches(password, player.getPassword())) {
            return jwtService.generateToken(Map.of(), player.getUsername());
        }
        return "";
    }
}
