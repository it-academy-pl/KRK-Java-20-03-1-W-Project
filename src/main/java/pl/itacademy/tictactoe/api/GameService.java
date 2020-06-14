package pl.itacademy.tictactoe.api;

import pl.itacademy.tictactoe.domain.*;
import pl.itacademy.tictactoe.exception.GameNotFoundException;
import pl.itacademy.tictactoe.exception.IllegalMoveException;
import pl.itacademy.tictactoe.exception.InvalidPasswordException;
import pl.itacademy.tictactoe.exception.PlayerNotFoundException;

import java.util.Optional;

import static pl.itacademy.tictactoe.domain.GameState.*;

public class GameService implements GameInterface {
    private final GameRepository repository;

    public GameService(GameRepository repository) {
        this.repository = repository;
    }

    @Override
    public GameResponse registerPlayer(Player player) {
        Optional<Game> waitingGame = repository.getWaitingGame();
        Game game;
        if (waitingGame.isEmpty()) {
            game = new Game();
            game.setXPlayer(player);
            game.setState(WAITING_FOR_REGISTRATION);
            repository.addGame(game);
        } else {
            game = waitingGame.get();
            game.setOPlayer(player);
            game.setState(X_MOVE);
        }
        return GameResponse.from(game);
    }

    @Override
    public GameResponse makeMove(Move move) {
        Game game = repository.getGameById(move.getGameId())
                .orElseThrow(() -> new GameNotFoundException("Game [" + move.getGameId() + "] not found"));

        char cellValue = game.getBoard()[move.getCellIndex()];
        if (cellValue > 0) {
            throw new IllegalMoveException("Board cell " + move.getCellIndex() +
                    " already contains value " + cellValue + ".");
        }

        char moveMadeBy;
        boolean validPassword;
        boolean validMove;
        GameState nextGameState;

        if (game.getXPlayer().getName().equals(move.getPlayer().getName())) {
            moveMadeBy = 'X';
            validMove = game.getState() == X_MOVE;
            validPassword = game.getXPlayer().getPassword().equals(move.getPlayer().getPassword());
            nextGameState = O_MOVE;
        } else if (game.getOPlayer().getName().equals(move.getPlayer().getName())) {
            moveMadeBy = 'O';
            validMove = game.getState() == O_MOVE;
            validPassword = game.getOPlayer().getPassword().equals(move.getPlayer().getPassword());
            nextGameState = X_MOVE;
        } else {
            throw new IllegalMoveException("Game [" + move.getGameId() + "] cannot be played by player "
                    + move.getPlayer().getName());
        }

        if (!validPassword) {
            throw new InvalidPasswordException("Player " + move.getPlayer().getName() + " provided invalid password");
        }

        switch (game.getState()) {
            case X_WON:
            case O_WON:
            case DRAW:
                throw new IllegalMoveException("Game [" + move.getGameId() + "] already finished with " + game.getState());
            case X_MOVE:
            case O_MOVE:
                if (!validMove) {
                    throw new IllegalMoveException("Move made by player " + moveMadeBy +
                            " but " + game.getState() + " expected.");
                }
        }

        game.getBoard()[move.getCellIndex()] = moveMadeBy;
        game.setState(nextGameState);
        return GameResponse.from(game);
    }

    @Override
    public GameResponse getGameState(int gameId) {
        return GameResponse.from(repository.getGameById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game [" + gameId + "] not found")));
    }

    @Override
    public GameResponse playAgain(int gameId) {
        Game game = repository.getGameById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game [" + gameId + "] not found"));
        Game newGame = new Game(game.getOPlayer(), game.getXPlayer(), X_MOVE);
        newGame = repository.addGame(newGame);

        return GameResponse.from(newGame);
    }

    @Override
    public GameStatistics getGameStatistic(Player player) {
        repository.games().stream()
                .filter(game -> game.getXPlayer().equals(player) || game.getOPlayer().equals(player))
                .findFirst()
                .orElseThrow(() -> new PlayerNotFoundException("Player " + player.getName() + " not found."));


        int wonAsXPlayer = Math.toIntExact(repository.games().stream()
                .filter(game -> game.getXPlayer().equals(player))
                .filter(game -> game.getState().equals(X_WON))
                .count());

        int lostAsXPlayer = Math.toIntExact(repository.games().stream()
                .filter(game -> game.getXPlayer().equals(player))
                .filter(game -> game.getState().equals(O_WON))
                .count());
        int drawAsXPlayer = Math.toIntExact(repository.games().stream()
                .filter(game -> game.getXPlayer().equals(player))
                .filter(game -> game.getState().equals(DRAW))
                .count());

        int wonAsOPlayer = Math.toIntExact(repository.games().stream()
                .filter(game -> game.getOPlayer().equals(player))
                .filter(game -> game.getState().equals(O_WON))
                .count());
        int lostAsOPlayer = Math.toIntExact(repository.games().stream()
                .filter(game -> game.getOPlayer().equals(player))
                .filter(game -> game.getState().equals(X_WON))
                .count());

        int drawAsOPlayer = Math.toIntExact(repository.games().stream()
                .filter(game -> game.getOPlayer().equals(player))
                .filter(game -> game.getState().equals(DRAW))
                .count());

        return new GameStatistics(
                wonAsXPlayer + wonAsOPlayer,
                lostAsXPlayer + lostAsOPlayer,
                drawAsXPlayer + drawAsOPlayer);

    }

}