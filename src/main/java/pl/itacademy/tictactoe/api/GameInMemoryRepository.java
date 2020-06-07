package pl.itacademy.tictactoe.api;

import pl.itacademy.tictactoe.domain.Game;

import java.util.*;

public class GameInMemoryRepository implements GameRepository {
    private Map<Integer, Game> games = new HashMap<>();

    @Override
    public Game addGame(Game game) {
        games.put(game.getId(), game);
        return game;
    }

    @Override
    public Optional<Game> getGameById(Integer id) {
        return Optional.ofNullable(games.get(id));
    }

    @Override
    public Optional<Game> updateGame(Game game) {
        return Optional.ofNullable(games.replace(game.getId(), game));
    }

    @Override
    public Collection<Game> games() {
        return games.values();
    }
}
