package pl.itacademy.tictactoe.api;

import pl.itacademy.tictactoe.domain.Game;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GameInMemoryRepository implements GameRepository {
    private Map<Integer, Game> games = new HashMap<>();

    @Override
    public Game addGame(Game game) {
        return null;
    }

    @Override
    public Optional<Game> getGameById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Game updateGame(Game game) {
        return null;
    }
}
