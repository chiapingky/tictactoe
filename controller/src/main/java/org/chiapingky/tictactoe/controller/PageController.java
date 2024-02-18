package org.chiapingky.tictactoe.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.chiapingky.tictactoe.account.AccountService;
import org.chiapingky.tictactoe.auth.JwtService;
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
    @Autowired
    private JwtService jwtService;

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
            @CookieValue(value = "token", defaultValue = "") String tokenCookie,
            HttpServletResponse response
    ) {
        String token = (String) model.getAttribute("token");
        String username;
        try {
            Cookie cookie;
            if (token != null && !token.isEmpty()) {
                username = jwtService.extractUsernameFromToken(token);
                cookie = generateCookie(token);
            } else if (!tokenCookie.isEmpty()) {
                username = jwtService.extractUsernameFromToken(tokenCookie);
                cookie = generateCookie(tokenCookie);
            } else {
                return "redirect:/login";
            }
            response.addCookie(cookie);
            model.addAttribute("username", username);
        } catch (Exception e) {
            return "redirect:/login";
        }
        Game currentActive = gameRepository.findCurrentActiveGame(username, GameStatus.ONGOING).orElse(null);
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
        String result = accountService.register(registerForm.getUsername(), registerForm.getPassword());
        if (result.isEmpty()) {
            model.addAttribute("error", "Username taken. Use different username");
            return "registration";
        }
        redirectAttributes.addFlashAttribute("token", result);
        return "redirect:/home";
    }

    @PostMapping("/login")
    public String submitLogin(
            LoginForm loginForm,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        String result = accountService.authenticate(loginForm.getUsername(), loginForm.getPassword());
        if (result == null) {
            model.addAttribute("error", "Failed to login. Please check your username and password");
            return "login";
        }
        redirectAttributes.addFlashAttribute("token", result);
        return "redirect:/home";
    }

    @PostMapping("/logout")
    public String submitLogout(
            HttpServletResponse response
    ) {
        Cookie cookie = invalidateCookie();
        response.addCookie(cookie);
        return "redirect:/login";
    }

    @PostMapping("/create")
    public String newGame(
            NewGameForm newGameForm,
            RedirectAttributes redirectAttributes,
            @CookieValue(value = "token", defaultValue = "") String tokenCookie
    ) {
        if (tokenCookie == null || tokenCookie.isEmpty())
            return "redirect:/login";
        try {
            String username = jwtService.extractUsernameFromToken(tokenCookie);
            Game result = gameplayService.newGame(username, newGameForm.getGridSize(), newGameForm.getDifficulty());
            redirectAttributes.addFlashAttribute("game", result);
            redirectAttributes.addFlashAttribute("username", username);
            redirectAttributes.addFlashAttribute("action", "create");
            return "redirect:/board";
        } catch (Exception e) {
            LoggerFactory.getLogger(PageController.class).error("Failed to create room: ", e);
            redirectAttributes.addFlashAttribute("errorCreate", "Error on create room");
            return "redirect:/home";
        }
    }

    @PostMapping("/join/{gameId}")
    public String joinGame(
            @CookieValue(value = "token", defaultValue = "") String tokenCookie,
            @PathVariable("gameId") String gameId,
            RedirectAttributes redirectAttributes
    ) {
        if (tokenCookie == null || tokenCookie.isEmpty())
            return "redirect:/login";
        try {
            String username = jwtService.extractUsernameFromToken(tokenCookie);
            Game result = gameplayService.joinGame(gameId, username);
            redirectAttributes.addFlashAttribute("game", result);
            redirectAttributes.addFlashAttribute("username", username);
            redirectAttributes.addFlashAttribute("action", "join");
            return "redirect:/board";
        } catch (Exception e) {
            LoggerFactory.getLogger(PageController.class).error("Failed to join room: ", e);
            redirectAttributes.addFlashAttribute("errorCreate", "Error on joining room");
            return "redirect:/home";
        }
    }

    @PostMapping("/leave")
    public String leaveGame(
            @CookieValue(value = "token", defaultValue = "") String tokenCookie
    ) {
        if (tokenCookie == null || tokenCookie.isEmpty())
            return "redirect:/login";
        return "redirect:/home";
    }

    private Cookie generateCookie(String token) {
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 30); //30 minutes of inactivity
        return cookie;
    }

    private Cookie invalidateCookie() {
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        return cookie;
    }
}
