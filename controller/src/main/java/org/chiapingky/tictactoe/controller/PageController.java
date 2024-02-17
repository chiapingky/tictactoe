package org.chiapingky.tictactoe.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.chiapingky.tictactoe.account.AccountService;
import org.chiapingky.tictactoe.controller.model.game.Room;
import org.chiapingky.tictactoe.controller.model.page.JoinRequest;
import org.chiapingky.tictactoe.controller.model.page.NewGameForm;
import org.chiapingky.tictactoe.controller.model.page.LoginForm;
import org.chiapingky.tictactoe.controller.model.page.RegisterForm;
import org.chiapingky.tictactoe.game.Game;
import org.chiapingky.tictactoe.game.GameDifficulty;
import org.chiapingky.tictactoe.game.GameRepository;
import org.chiapingky.tictactoe.game.GameStatus;
import org.chiapingky.tictactoe.gameplay.GameplayService;
import org.chiapingky.tictactoe.user.Player;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class PageController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameplayService gameplayService;

    @GetMapping({"/", "/login"})
    public String loginPage(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "registration";
    }

    @GetMapping("/home")
    public String homePage(
            Model model,
            RedirectAttributes redirectAttributes,
            @CookieValue(value = "username", defaultValue = "") String cookieUsername,
            HttpServletResponse response
    ) {
        String username = (String) model.getAttribute("username");
        if (username != null && !username.isEmpty()) {
            Cookie cookie = generateCookie(username);
            response.addCookie(cookie);
        } else if (!cookieUsername.isEmpty()) {
            Cookie cookie = generateCookie(cookieUsername);
            response.addCookie(cookie);
            model.addAttribute("username", cookieUsername);
        }
        Game currentActive = gameRepository.findCurrentActiveGame(cookieUsername, GameStatus.ONGOING).orElse(null);
        if (currentActive != null) {
            redirectAttributes.addFlashAttribute("game", currentActive);
            redirectAttributes.addFlashAttribute("username", username);
            redirectAttributes.addFlashAttribute("action", "join");
            return "redirect:/board";
        }
        List<Game> activeGames = gameRepository.findByGameStatus(GameStatus.ONGOING);
        List<Room> result = new ArrayList<>();
        for (Game game : activeGames) {
            String owner = game.getCrossPlayer() != null? game.getCrossPlayer() : game.getRoundPlayer() != null? game.getRoundPlayer() : "";
            if (owner.isEmpty())
                continue;
            result.add(new Room(game.getGameId(), owner, game.getGameDifficulty().name()));
        }
        model.addAttribute("rooms", result);
        List<String> diffList = Arrays.stream(GameDifficulty.values()).map(Enum::name).toList();
        model.addAttribute("diffList", diffList);
        model.addAttribute("newGameForm", new NewGameForm());
        model.addAttribute("joinRequest", new JoinRequest());
        return "home";
    }

    @GetMapping("/board")
    public String boardPage(Model model) {
        Game game = (Game) model.getAttribute("game");
        String username = (String) model.getAttribute("username");
        String action = (String) model.getAttribute("action");
        if (game == null || username == null || username.isEmpty() || action == null || action.isEmpty()) {
            return "redirect:/login";
        }
        model.addAttribute("game", game);
        model.addAttribute("username", username);
        model.addAttribute("action", action);
        return "board";
    }

    @PostMapping("/register")
    public String submitRegistration(
            RegisterForm registerForm,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Player result = accountService.register(registerForm.getUsername(), registerForm.getPassword());
        if (result == null) {
            model.addAttribute("error", "Username taken. Use different username");
            return "registration";
        }
        redirectAttributes.addFlashAttribute("username", result.getUserName());
        return "redirect:/home";
    }

    @PostMapping("/login")
    public String submitLogin(
            LoginForm loginForm,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Player result = accountService.authenticate(loginForm.getUsername(), loginForm.getPassword());
        if (result == null) {
            model.addAttribute("error", "Failed to login. Please check your username and password");
            return "login";
        }
        redirectAttributes.addFlashAttribute("username", result.getUserName());
        return "redirect:/home";
    }

    @PostMapping("/logout")
    public String submitLogout(
            @CookieValue(value = "username", defaultValue = "") String username,
            HttpServletResponse response
    ) {
        if (!username.isEmpty()) {
            Cookie cookie = invalidateCookie(username);
            response.addCookie(cookie);
        }
        return "redirect:/login";
    }

    @PostMapping("/create")
    public String newGame(
            NewGameForm newGameForm,
            RedirectAttributes redirectAttributes,
            @CookieValue(value = "username", defaultValue = "") String username
    ) {
        if (username == null || username.isEmpty())
            return "redirect:/login";
        try {
            Game result = gameplayService.newGame(username, newGameForm.getGridSize(), newGameForm.getDifficulty());
            redirectAttributes.addFlashAttribute("game", result);
            redirectAttributes.addFlashAttribute("username", username);
            redirectAttributes.addFlashAttribute("action", "create");
            return "redirect:/board";
        } catch (Exception e) {
            LoggerFactory.getLogger(PageController.class).error("Failed to create room: ", e);
            redirectAttributes.addFlashAttribute("username", username);
            redirectAttributes.addFlashAttribute("errorCreate", "Error on create room");
            return "redirect:/home";
        }
    }

    @PostMapping("/join/{gameId}")
    public String joinGame(
            @CookieValue(value = "username", defaultValue = "") String username,
            @PathVariable("gameId") String gameId,
            RedirectAttributes redirectAttributes
    ) {
        if (username == null || username.isEmpty())
            return "redirect:/login";
        try {
            Game result = gameplayService.joinGame(gameId, username);
            redirectAttributes.addFlashAttribute("game", result);
            redirectAttributes.addFlashAttribute("username", username);
            redirectAttributes.addFlashAttribute("action", "join");
            return "redirect:/board";
        } catch (Exception e) {
            LoggerFactory.getLogger(PageController.class).error("Failed to join room: ", e);
            redirectAttributes.addFlashAttribute("username", username);
            redirectAttributes.addFlashAttribute("errorCreate", "Error on joining room");
            return "redirect:/home";
        }
    }

    @PostMapping("/leave")
    public String leaveGame(
            @CookieValue(value = "username", defaultValue = "") String username,
            RedirectAttributes redirectAttributes
    ) {
        if (username == null || username.isEmpty())
            return "redirect:/login";
        redirectAttributes.addFlashAttribute("username", username);
        return "redirect:/home";
    }

    private Cookie generateCookie(String username) {
        Cookie cookie = new Cookie("username", username);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 30); //30 minutes of inactivity
        return cookie;
    }

    private Cookie invalidateCookie(String username) {
        Cookie cookie = new Cookie("username", username);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); //30 minutes of inactivity
        return cookie;
    }
}
