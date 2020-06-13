package pl.itacademy.tictactoe.api;

import pl.itacademy.tictactoe.domain.GameResponse;
import pl.itacademy.tictactoe.domain.GameStatistics;
import pl.itacademy.tictactoe.domain.Move;
import pl.itacademy.tictactoe.domain.Player;

public class GameService implements GameInterface {
    @Override
    public GameResponse registerPlayer(Player player) {
        return null;
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
