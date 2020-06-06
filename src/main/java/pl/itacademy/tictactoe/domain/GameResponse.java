package pl.itacademy.tictactoe.domain;

import lombok.Value;


@Value
public class GameResponse {
    int gameId;
    GameState state;
    char[] board;
}
