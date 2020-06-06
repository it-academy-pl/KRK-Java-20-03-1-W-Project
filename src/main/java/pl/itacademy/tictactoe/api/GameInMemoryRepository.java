package pl.itacademy.tictactoe.api;

import pl.itacademy.tictactoe.domain.Game;

import java.util.HashMap;
import java.util.Map;

public class GameInMemoryRepository {
    private Map<Integer, Game> games = new HashMap<>();
}
