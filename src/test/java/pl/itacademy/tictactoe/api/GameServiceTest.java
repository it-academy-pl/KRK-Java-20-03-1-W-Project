package pl.itacademy.tictactoe.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.itacademy.tictactoe.domain.*;
import pl.itacademy.tictactoe.exception.GameNotFoundException;
import pl.itacademy.tictactoe.exception.IllegalMoveException;
import pl.itacademy.tictactoe.exception.InvalidPasswordException;
import pl.itacademy.tictactoe.exception.PlayerNotFoundException;

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

    @Test
    public void makeMove_cellNotEmpty_throwsIllegalMoveException() {
        Player firstPlayer = new Player("Oleg", "kow@lsk!1");
        Player secondPlayer = new Player("Daryna", "Qwer1234");
        Game game = new Game();
        game.setXPlayer(firstPlayer);
        game.setOPlayer(secondPlayer);
        game.setState(X_MOVE);
        gameRepository.addGame(game);

        Move firstMove = new Move(game.getId(), 0, firstPlayer);
        gameService.makeMove(firstMove);
        Move secondMove = new Move(game.getId(), 0, secondPlayer);

        IllegalMoveException exception = assertThrows(IllegalMoveException.class, () -> gameService.makeMove(secondMove));

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

        assertThat(exception.getMessage()).contains("X_WON");
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

        assertThat(exception.getMessage()).contains("player X");
        assertThat(exception.getMessage()).contains(O_MOVE.toString());
    }

    @Test //extra test for move by a third player
    public void makeMove_moveByThirdPlayer_throwsIllegalMoveException() {
        Player firstPlayer = new Player("Oleg", "kow@lsk!1");
        Player secondPlayer = new Player("Daryna", "Qwer1234");
        Game game = new Game();
        game.setXPlayer(firstPlayer);
        game.setOPlayer(secondPlayer);
        game.setState(X_MOVE);
        gameRepository.addGame(game);

        Player thirdPlayer = new Player("Artek", "12345678");
        Move move = new Move(game.getId(), 0, thirdPlayer);
        IllegalMoveException exception = assertThrows(IllegalMoveException.class, () -> gameService.makeMove(move));
        assertThat(exception.getMessage()).contains(thirdPlayer.getName());
    }

    @Test
    public void makeMove_wrongPassword_throwsWrongPasswordException() {
        Player firstPlayer = new Player("Oleg", "kow@lsk!1");
        Game game = new Game();
        game.setXPlayer(firstPlayer);
        game.setState(X_MOVE);
        gameRepository.addGame(game);

        Player wrongPasswordUser = new Player("Oleg", "12345678");
        Move move = new Move(game.getId(), 0, wrongPasswordUser);
        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> gameService.makeMove(move));
        assertThat(exception.getMessage()).contains(firstPlayer.getName());
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
        Game game = new Game();
        game.setXPlayer(firstPlayer);
        game.setState(X_MOVE);
        gameRepository.addGame(game);

        assertThat(gameService.getGameState(game.getId())).isEqualTo(GameResponse.from(game));
    }

    @Test
    public void getGameState_nonExistingGameId_throwsGameNotFoundException() {
        Player firstPlayer = new Player("Oleg", "kow@lsk!1");
        Game game = new Game();
        game.setXPlayer(firstPlayer);
        game.setState(X_MOVE);
        gameRepository.addGame(game);

        GameNotFoundException exception = assertThrows(GameNotFoundException.class, () -> gameService.getGameState(2));
        assertThat(exception.getMessage()).contains("2");
    }

    @Test
    public void gameStatistic_playerHasGames_returnPlayerStatistic() {
        Player firstPlayer = new Player("Oleg", "kow@lsk!1");
        Player secondPlayer = new Player("Daryna", "Qwer1234");
        Game game1 = new Game();
        game1.setXPlayer(firstPlayer);
        game1.setOPlayer(secondPlayer);
        game1.setState(X_WON);
        gameRepository.addGame(game1);

        Game game2 = new Game();
        game2.setXPlayer(firstPlayer);
        game2.setOPlayer(secondPlayer);
        game2.setState(O_WON);
        gameRepository.addGame(game2);

        Game game3 = new Game();
        game3.setXPlayer(firstPlayer);
        game3.setOPlayer(secondPlayer);
        game3.setState(DRAW);
        gameRepository.addGame(game3);

        assertThat(gameService.getGameStatistic(firstPlayer))
                .isEqualTo(new GameStatistics(1, 1, 1));

        assertThat(gameService.getGameStatistic(secondPlayer))
                .isEqualTo(new GameStatistics(1, 1, 1));
    }

    @Test
    public void gameStatistic_playerHasNoGames_throwsPlayerNotFoundException() {
        Player firstPlayer = new Player("Oleg", "kow@lsk!1");
        Player secondPlayer = new Player("Daryna", "Qwer1234");
        Game game1 = new Game();
        game1.setXPlayer(firstPlayer);
        game1.setOPlayer(secondPlayer);
        game1.setState(X_WON);
        gameRepository.addGame(game1);

        Player thirdPlayer = new Player("Artek", "password");
        PlayerNotFoundException exception = assertThrows(PlayerNotFoundException.class, () -> gameService.getGameStatistic(thirdPlayer));

        assertThat(exception.getMessage()).contains(thirdPlayer.getName());
    }

    @Test
    public void playAgain_gameIdNotExists_throwsGameNotFoundException() {
        Player firstPlayer = new Player("Oleg", "kow@lsk!1");
        Player secondPlayer = new Player("Daryna", "Qwer1234");
        Game game = new Game();
        game.setXPlayer(firstPlayer);
        game.setOPlayer(secondPlayer);
        game.setState(X_WON);
        gameRepository.addGame(game);

        GameNotFoundException exception = assertThrows(GameNotFoundException.class, () -> gameService.playAgain(2));
        assertThat(exception.getMessage()).contains("2");
    }

    @Test
    public void playAgain_createsNewGameWithTheSamePlayers_and_stateX_MOVE() {
        Player firstPlayer = new Player("Oleg", "kow@lsk!1");
        Player secondPlayer = new Player("Daryna", "Qwer1234");
        Game game = new Game();
        game.setXPlayer(firstPlayer);
        game.setOPlayer(secondPlayer);
        game.setState(X_WON);
        gameRepository.addGame(game);
        GameResponse gameResponse = gameService.playAgain(game.getId());
        assertThat(gameRepository.getGameById(gameResponse.getGameId()).get().getXPlayer()).isEqualTo(secondPlayer);
        assertThat(gameRepository.getGameById(gameResponse.getGameId()).get().getXPlayer()).isEqualTo(secondPlayer);
        assertThat(gameResponse.getState()).isEqualTo(X_MOVE);
    }
}