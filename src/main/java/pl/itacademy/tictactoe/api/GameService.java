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

        checkCellIndex(move.getCellIndex(), game);

        checkPlayer(move.getPlayer(), game);

        char moveChar = getMoveChar(move.getPlayer(), game);
        checkMove(moveChar, game);

        game.getBoard()[move.getCellIndex()] = moveChar;
        GameState gameStateAfterMove = getGameStateAfterMove(moveChar, game);
        game.setState(gameStateAfterMove);
        return GameResponse.from(game);
    }

    private void checkCellIndex(int cellIndex, Game game) {
        char cellValue = game.getBoard()[cellIndex];
        if (cellValue == 'X' || cellValue == 'O') {
            throw new IllegalMoveException("Board cell " + cellIndex +
                    " already contains value " + cellValue + ".");
        }
    }

    private void checkPlayer(Player player, Game game) {
        Player xPlayer = game.getXPlayer();
        Player oPlayer = game.getOPlayer();
        boolean validPassword;

        if (xPlayer.getName().equals(player.getName())) {
            validPassword = xPlayer.getPassword().equals(player.getPassword());
        } else if (oPlayer.getName().equals(player.getName())) {
            validPassword = oPlayer.getPassword().equals(player.getPassword());
        } else {
            throw new PlayerNotFoundException("Player " + player.getName()
                    + " not found in game [" + game.getId() + "].");
        }

        if (!validPassword) {
            throw new InvalidPasswordException("Player " + player.getName() + " provided invalid password");
        }
    }

    private void checkMove(char moveChar, Game game) {
        boolean validMove = false;
        if (moveChar == 'X') {
            validMove = game.getState() == X_MOVE;
        } else if (moveChar == 'O') {
            validMove = game.getState() == O_MOVE;
        }

        switch (game.getState()) {
            case X_WON:
            case O_WON:
            case DRAW:
                throw new IllegalMoveException("Game [" + game.getId() + "] already finished with " + game.getState());
            case X_MOVE:
            case O_MOVE:
                if (!validMove) {
                    throw new IllegalMoveException("Move made by player " + moveChar +
                            " but " + game.getState() + " expected.");
                }
            case WAITING_FOR_REGISTRATION:
        }

    }

    private char getMoveChar(Player player, Game game) {
        Player xPlayer = game.getXPlayer();
        Player oPlayer = game.getOPlayer();

        char moveChar = 0;
        if (xPlayer.getName().equals(player.getName())) {
            moveChar = 'X';
        } else if (oPlayer.getName().equals(player.getName())) {
            moveChar = 'O';
        }
        return moveChar;
    }

    //
    private GameState getGameStateAfterMove(char moveChar, Game game) {
        char[] board = game.getBoard();
        GameState nextGameState = DRAW;
        boolean draw = true;
        boolean won = false;
        int i = 0;
        do {
            draw = (board[i] == 'X' || board[i] == 'O');
            i++;
        } while (draw && i < 9);

        if (!draw) {
            won = (board[0] == moveChar && board[1] == moveChar && board[2] == moveChar) ||
                    (board[3] == moveChar && board[4] == moveChar && board[5] == moveChar) ||
                    (board[6] == moveChar && board[7] == moveChar && board[8] == moveChar) ||

                    (board[0] == moveChar && board[3] == moveChar && board[6] == moveChar) ||
                    (board[1] == moveChar && board[4] == moveChar && board[7] == moveChar) ||
                    (board[2] == moveChar && board[5] == moveChar && board[8] == moveChar) ||

                    (board[0] == moveChar && board[4] == moveChar && board[8] == moveChar) ||
                    (board[6] == moveChar && board[4] == moveChar && board[2] == moveChar);

            if (moveChar == 'X') {
                nextGameState = won ? X_WON : O_MOVE;
            }

            if (moveChar == 'O') {
                nextGameState = won ? O_WON : X_MOVE;
            }
        }
        return nextGameState;
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