package pl.itacademy.tictactoe.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.itacademy.tictactoe.domain.Game;
import pl.itacademy.tictactoe.domain.GameResponse;
import pl.itacademy.tictactoe.domain.Player;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.itacademy.tictactoe.domain.GameState.WAITING_FOR_REGISTRATION;
import static pl.itacademy.tictactoe.domain.GameState.X_MOVE;

class GameServiceTest {

    private GameService gameService;
    private GameRepository gameRepository;

    @BeforeEach
    public void setUp() {
        gameRepository = new GameInMemoryRepository();
        gameService = new GameService(gameRepository);
    }

    @Test
    public void registerPlayer_noWaitingGames_createsNewGame_and_registerPlayerToIt() {
        Player firstPlayer = new Player("Jan", "kow@lsk!");
        GameResponse gameResponse = gameService.registerPlayer(firstPlayer);

        assertThat(gameResponse.getState()).isEqualTo(WAITING_FOR_REGISTRATION);

        int gameId = gameResponse.getGameId();
        Game game = gameRepository.getGameById(gameId).orElseThrow();
        assertThat(game.getXPlayer()).isEqualTo(firstPlayer);
    }

    @Test
    public void registerPlayer_hasWaitingGames_addsPlayerToWaitingGame_and_startsTheGame() {
        Player firstPlayer = new Player("Oleg", "kow@lsk!1");
        Game game = new Game();
        game.setXPlayer(firstPlayer);
        game.setState(WAITING_FOR_REGISTRATION);
        gameRepository.addGame(game);

        Player secondPlayer = new Player("Daryna", "Qwer1234");

        GameResponse gameResponse = gameService.registerPlayer(secondPlayer);

        assertThat(gameResponse.getState()).isEqualTo(X_MOVE);


    }

}