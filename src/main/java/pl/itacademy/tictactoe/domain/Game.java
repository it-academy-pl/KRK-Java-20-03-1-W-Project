package pl.itacademy.tictactoe.domain;

import lombok.Data;


@Data
public class Game {
    private Integer id;
    private char[] board;
    private Player xPlayer;
    private Player oPlayer;
    private GameState state;
}
