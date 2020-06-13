package pl.itacademy.tictactoe.api;

import pl.itacademy.tictactoe.domain.*;

public class GameService implements GameInterface {
    private final GameRepository repository;

    public GameService(GameRepository repository) {
        this.repository = repository;
    }

    @Override
    public GameResponse registerPlayer(Player player) {
        Game game = new Game();
        game.setXPlayer(player);
        repository.addGame(game);
        return new GameResponse(game.getId(), GameState.WAITING_FOR_REGISTRATION, game.getBoard());
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
