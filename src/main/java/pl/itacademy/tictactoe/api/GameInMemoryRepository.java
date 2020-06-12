package pl.itacademy.tictactoe.api;

import pl.itacademy.tictactoe.domain.Game;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GameInMemoryRepository implements GameRepository {
    private Map<Integer, Game> games = new HashMap<>();

    @Override
    public Game addGame(Game game) {
        if (games.isEmpty()) {
            game.setId(1);
        } else {
            int maxKey = games.keySet().stream().max(Comparator.naturalOrder()).get();
            game.setId(++maxKey);
        }
        games.put(game.getId(), game);
        return game;
    }

    @Override
    public Optional<Game> getGameById(Integer id) {
        if (!games.containsKey(id)) {
            return Optional.empty();
        }
        return Optional.of(games.get(id));
    }

    @Override
    public Game updateGame(Game game) {
        if (game.getId() == 0) {
            //TODO: throw exception
        }
        games.put(game.getId(), game);
        return game;
    }
}
