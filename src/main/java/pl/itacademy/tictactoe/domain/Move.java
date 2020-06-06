package pl.itacademy.tictactoe.domain;

import lombok.Data;

@Data
public class Move {
    private int gameId;
    private int cellIndex;
    private Player player;
}
