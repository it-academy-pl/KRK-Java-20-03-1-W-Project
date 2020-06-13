package pl.itacademy.tictactoe.domain;

import java.util.Objects;

public class Move {
    private int gameId;
    private int cellIndex;
    private Player player;

    public Move(int gameId, int cellIndex, Player player) {
        this.gameId = gameId;
        this.cellIndex = cellIndex;
        this.player = player;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getCellIndex() {
        return cellIndex;
    }

    public void setCellIndex(int cellIndex) {
        this.cellIndex = cellIndex;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return gameId == move.gameId &&
                cellIndex == move.cellIndex &&
                player.equals(move.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId, cellIndex, player);
    }

    @Override
    public String toString() {
        return "Move{" +
                "gameId=" + gameId +
                ", cellIndex=" + cellIndex +
                ", player=" + player +
                '}';
    }
}
