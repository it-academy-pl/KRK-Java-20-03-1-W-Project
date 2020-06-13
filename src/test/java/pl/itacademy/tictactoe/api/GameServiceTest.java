package pl.itacademy.tictactoe.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.itacademy.tictactoe.domain.Game;
import pl.itacademy.tictactoe.domain.GameResponse;
import pl.itacademy.tictactoe.domain.Move;
import pl.itacademy.tictactoe.domain.Player;
import pl.itacademy.tictactoe.exception.GameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static pl.itacademy.tictactoe.domain.GameState.*;

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
        assertThat(game.getState()).isEqualTo(WAITING_FOR_REGISTRATION);
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
        Game actual = gameRepository.getGameById(game.getId()).orElseThrow();
        assertThat(actual.getState()).isEqualTo(X_MOVE);
    }

    @Test
    public void makeMove_xMoveGameState_xPlayerMakesSuccessfulMove() {
        Player firstPlayer = new Player("Oleg", "kow@lsk!1");
        Player secondPlayer = new Player("Daryna", "Qwer1234");
        Game game = new Game();
        game.setXPlayer(firstPlayer);
        game.setOPlayer(secondPlayer);
        game.setState(X_MOVE);
        gameRepository.addGame(game);

        Move move = new Move(game.getId(), 0, firstPlayer);
        GameResponse gameResponse = gameService.makeMove(move);
        assertThat(gameResponse.getState()).isEqualTo(O_MOVE);
        assertThat(gameResponse.getBoard()[0]).isEqualTo('X');
    }

    @Test
    public void makeMove_nonExistingGameID_throwsGameNotFoundException() {
        Player firstPlayer = new Player("Oleg", "kow@lsk!1");

        Move move = new Move(1, 0, firstPlayer);
        GameNotFoundException exception = assertThrows(GameNotFoundException.class, () -> gameService.makeMove(move));

        assertThat(exception.getMessage()).contains("1");

    }
}