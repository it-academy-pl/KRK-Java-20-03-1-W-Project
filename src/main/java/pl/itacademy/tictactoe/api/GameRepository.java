package pl.itacademy.tictactoe.api;

import pl.itacademy.tictactoe.domain.Game;

public interface GameRepository {
    Game addGame (Game game);
    Game getGameById (Integer id);
    Game updateGame (Game game);
}
