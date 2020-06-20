package pl.itacademy.tictactoe.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.itacademy.tictactoe.domain.GameResponse;
import pl.itacademy.tictactoe.domain.Player;

@Controller
@RequestMapping("/game")
public class GameController {

    private final GameInterface gameInterface;

    public GameController(GameInterface gameInterface) {
        this.gameInterface = gameInterface;
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameResponse> getGameState(@PathVariable("gameId") Integer gameId) {
        return ResponseEntity.ok(gameInterface.getGameState(gameId));
    }

    @PostMapping("/register")
    public ResponseEntity<GameResponse> registerPlayer(@RequestBody Player player) {
        return ResponseEntity.ok(gameInterface.registerPlayer(player));
    }
}
