package pl.itacademy.tictactoe.api;

import pl.itacademy.tictactoe.domain.*;

import java.util.Optional;

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
            game.setState(GameState.WAITING_FOR_REGISTRATION);
            repository.addGame(game);
        } else {
            game = waitingGame.get();
            game.setOPlayer(player);
            game.setState(GameState.X_MOVE);
        }
        return GameResponse.from(game);
    }

    @Override
    public GameResponse makeMove(Move move) {
        return null;
    }

    @Override
    public GameResponse getGameState(int gameId) {
        return null;
    }

    @Override
    public GameResponse playAgain(int gameId) {
        return null;
    }

    @Override
    public GameStatistics getGameStatistic(Player player) {
        return null;
    }
}