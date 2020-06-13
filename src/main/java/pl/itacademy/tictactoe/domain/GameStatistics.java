package pl.itacademy.tictactoe.domain;

import java.util.Objects;

public class GameStatistics {
    private final int won;
    private final int lost;
    private final int draw;

    public GameStatistics(int won, int lost, int draw) {
        this.won = won;
        this.lost = lost;
        this.draw = draw;
    }

    public int getWon() {
        return won;
    }

    public int getLost() {
        return lost;
    }

    public int getDraw() {
        return draw;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameStatistics that = (GameStatistics) o;
        return won == that.won &&
                lost == that.lost &&
                draw == that.draw;
    }

    @Override
    public int hashCode() {
        return Objects.hash(won, lost, draw);
    }

    @Override
    public String toString() {
        return "GameStatistics{" +
                "won=" + won +
                ", lost=" + lost +
                ", draw=" + draw +
                '}';
    }
}
