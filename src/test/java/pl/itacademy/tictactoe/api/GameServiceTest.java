package pl.itacademy.tictactoe.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.itacademy.tictactoe.domain.Game;
import pl.itacademy.tictactoe.domain.GameResponse;
import pl.itacademy.tictactoe.domain.Move;
import pl.itacademy.tictactoe.domain.Player;
import pl.itacademy.tictactoe.exception.GameNotFoundException;
import pl.itacademy.tictactoe.exception.IllegalMoveException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    public void makeMove_cellNotEmpty_throwsIllegalMoveException() {
        Player firstPlayer = new Player("Oleg", "kow@lsk!1");
        Player secondPlayer = new Player("Daryna", "Qwer1234");
        Game game = new Game();
        game.setXPlayer(firstPlayer);
        game.setOPlayer(secondPlayer);
        game.setState(X_MOVE);
        gameRepository.addGame(game);
        Move move = new Move(game.getId(), 0, firstPlayer);
        gameService.makeMove(move);
        IllegalMoveException exception = assertThrows(IllegalMoveException.class, () -> gameService.makeMove(move));

        assertThat(exception.getMessage()).contains("0");

    }

    @Test
    public void makeMove_gameAlreadyFinished_throwsIllegalMoveException() {
        Player firstPlayer = new Player("Oleg", "kow@lsk!1");
        Player secondPlayer = new Player("Daryna", "Qwer1234");
        Game game = new Game();
        game.setXPlayer(firstPlayer);
        game.setOPlayer(secondPlayer);
        game.setState(X_WON);
        gameRepository.addGame(game);
        Move move = new Move(game.getId(), 0, firstPlayer);
        IllegalMoveException exception = assertThrows(IllegalMoveException.class, () -> gameService.makeMove(move));
        assertThat(exception.getMessage()).contains("game is already finished");

    }

    @Test
    public void makeXMove_gameExpectsOMove_throwsIllegalMoveException() {
        Player firstPlayer = new Player("Oleg", "kow@lsk!1");
        Player secondPlayer = new Player("Daryna", "Qwer1234");
        Game game = new Game();
        game.setXPlayer(firstPlayer);
        game.setOPlayer(secondPlayer);
        game.setState(O_MOVE);
        gameRepository.addGame(game);
        Move move = new Move(game.getId(), 0, firstPlayer);
        IllegalMoveException exception = assertThrows(IllegalMoveException.class, () -> gameService.makeMove(move));
        assertThat(exception.getMessage()).contains(move.getPlayer().getName());

    }

    @Test
    public void makeMove_wrongPassword_throwsWrongPasswordException() {

    }

//    @Test
//    public void makeXMove_playerWon_changesGameStateToX_WON() {
//
//    }
//
//    @Test
//    public void makeOMove_playerWon_changesGameStateToO_WON() {
//
//    }
//
//    @Test
//    public void makeMove_gameDrawAfterMove_changesGameStateToDRAW() {
//
//    }

    @Test
    public void getGameState_returnsGameResponse() {
        Player firstPlayer = new Player("Oleg", "kow@lsk!1");
        Player secondPlayer = new Player("Daryna", "Qwer1234");
        Game game = new Game();
        game.setXPlayer(firstPlayer);
        game.setOPlayer(secondPlayer);
        game.setState(O_MOVE);
        gameRepository.addGame(game);
        GameResponse gameResponse = gameService.getGameState(game.getId());
        assertEquals(gameResponse.getGameId(), game.getId());
        assertEquals(gameResponse.getState(), game.getState());
        assertEquals(gameResponse.getBoard(), game.getBoard());

    }
//aaaaa
    @Test
    public void getGameState_nonExistingGameId_throwsGameNotFoundException() {
        Player firstPlayer = new Player("Oleg", "kow@lsk!1");
        Player secondPlayer = new Player("Daryna", "Qwer1234");
        Game game = new Game();
        game.setXPlayer(firstPlayer);
        game.setOPlayer(secondPlayer);
        game.setState(O_MOVE);
        gameRepository.addGame(game);
        GameResponse gameResponse = gameService.getGameState(game.getId());
        GameNotFoundException exception = assertThrows(GameNotFoundException.class, () -> gameService.getGameState(5));
        assertThat(exception.getMessage()).contains("5");
    }

    @Test
    public void gameStatistic_playerHasGames_returnPlayerStatistic() {


    }

    @Test
    public void gameStatistic_playerHasNoGames_throwsPlayerNotFoundException() {

    }

    @Test
    public void playAgain_gameIdNotExists_throwsGameNotFoundException() {


    }

    @Test
    public void playAgain_createsNewGameWithTheSamePlayers_and_stateX_MOVE() {


    }
}