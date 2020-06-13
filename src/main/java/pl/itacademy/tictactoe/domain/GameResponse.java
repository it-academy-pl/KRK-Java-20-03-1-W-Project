package pl.itacademy.tictactoe.domain;

import java.util.Arrays;
import java.util.Objects;

public class GameResponse {
    private final int gameId;
    private final GameState state;
    private final char[] board;

    public GameResponse(int gameId, GameState state, char[] board) {
        this.gameId = gameId;
        this.state = state;
        this.board = board;
    }

    public int getGameId() {
        return gameId;
    }

    public GameState getState() {
        return state;
    }

    public char[] getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameResponse that = (GameResponse) o;
        return gameId == that.gameId &&
                state == that.state &&
                Arrays.equals(board, that.board);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(gameId, state);
        result = 31 * result + Arrays.hashCode(board);
        return result;
    }

    @Override
    public String toString() {
        return "GameResponse{" +
                "gameId=" + gameId +
                ", state=" + state +
                ", board=" + Arrays.toString(board) +
                '}';
    }
}
