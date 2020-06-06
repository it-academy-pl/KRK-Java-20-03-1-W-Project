package pl.itacademy.tictactoe.domain;

import lombok.Value;

@Value
public class GameStatistics {
    int won;
    int lost;
    int draw;

}
