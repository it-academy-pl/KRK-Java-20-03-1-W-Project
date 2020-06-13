package pl.itacademy.tictactoe.api;

import pl.itacademy.tictactoe.domain.Game;

import java.util.Collection;
import java.util.Optional;

public interface GameRepository {
    Game addGame(Game game);

    Optional<Game> getGameById(Integer id);

    Optional<Game> updateGame(Game game);

    Collection<Game> games();
}
