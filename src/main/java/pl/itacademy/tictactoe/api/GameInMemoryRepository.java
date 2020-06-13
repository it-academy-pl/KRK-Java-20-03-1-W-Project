package pl.itacademy.tictactoe.api;

import pl.itacademy.tictactoe.domain.Game;
import pl.itacademy.tictactoe.exception.GameNotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;

public class GameInMemoryRepository implements GameRepository {
    private Map<Integer, Game> games = new HashMap<>();
    private AtomicInteger idCounter = new AtomicInteger(0);

    @Override
    public Game addGame(Game game) {
        if (isNull(game.getId())) {
            game.setId(idCounter.getAndIncrement());
        }
        games.put(game.getId(), game);
        return game;
    }

    @Override
    public Optional<Game> getGameById(Integer id) {
        return Optional.ofNullable(games.get(id));
    }

    @Override
    public Game updateGame(Game game) {
        if (!games.containsKey(game.getId())) {
            throw new GameNotFoundException("Game [" + game.getId() + "] not found");
        }
        return games.put(game.getId(), game);
    }

    @Override
    public Collection<Game> games() {
        return games.values();
    }
}
