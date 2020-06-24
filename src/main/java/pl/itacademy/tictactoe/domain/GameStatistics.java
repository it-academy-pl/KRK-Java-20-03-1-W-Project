package pl.itacademy.tictactoe.domain;

import java.util.Objects;

public class GameStatistics {
    private int won;
    private int lost;
    private int draw;

    public GameStatistics() {

    }

    public GameStatistics(int won, int lost, int draw) {
        this.won = won;
        this.lost = lost;
        this.draw = draw;
    }

    public void accumulate(Game game, Player player) {
        if (game.getXPlayer().equals(player)) {
            switch (game.getState()) {
                case X_WON:
                    this.won += 1;
                    break;
                case O_WON:
                    this.lost += 1;
                    break;
                case DRAW:
                    this.draw += 1;
                    break;
            }
        }

        if (game.getOPlayer().equals(player)) {
            switch (game.getState()) {
                case X_WON:
                    this.lost += 1;
                    break;
                case O_WON:
                    this.won += 1;
                    break;
                case DRAW:
                    this.draw += 1;
                    break;
            }
        }
    }

    public void combine(GameStatistics other) {
        this.won += other.won;
        this.lost += other.lost;
        this.draw += other.draw;
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
