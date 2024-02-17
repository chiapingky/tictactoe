package org.chiapingky.tictactoe.account;

import org.chiapingky.tictactoe.user.Player;
import org.chiapingky.tictactoe.user.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService { //TODO add proper authentication and authorization
    @Autowired
    private PlayerRepository playerRepository;

    public Player register(String username, String password) {
        Player exist = playerRepository.findById(username).orElse(null);
        if (exist != null)
            return null;
        //TODO: check password strength
        //TODO: encrypt password
        return playerRepository.save(new Player(username, password));
    }

    public Player authenticate(String username, String password) {
        Player exist = playerRepository.findById(username).orElse(null);
        if (exist == null)
            return null;
        //TODO: compare encrypted if implement encryption
        if (!exist.getPassword().equals(password))
            return null;
        return exist;
    }
}
