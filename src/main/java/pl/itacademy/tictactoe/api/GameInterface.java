package pl.itacademy.tictactoe.api;

import pl.itacademy.tictactoe.domain.*;

public interface GameInterface {
    GameResponse registerPlayer(Player player);
    GameResponse makeMove(Move move);
    GameResponse getGameState(int gameId);
    GameResponse playAgain(int gameId);
    GameStatistics getGameStatistic(Player player);

}
