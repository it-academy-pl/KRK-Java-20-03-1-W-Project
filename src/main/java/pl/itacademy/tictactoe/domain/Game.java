package pl.itacademy.tictactoe.domain;

import java.util.Arrays;
import java.util.Objects;

public class Game {
    private Integer id;
    private char[] board;
    private Player xPlayer;
    private Player oPlayer;
    private GameState state;

    public Game(Integer id, char[] board, Player xPlayer, Player oPlayer, GameState state) {
        this.id = id;
        this.board = board;
        this.xPlayer = xPlayer;
        this.oPlayer = oPlayer;
        this.state = state;
    }

    public Game(char[] board, Player xPlayer, Player oPlayer, GameState state) {
        this.board = board;
        this.xPlayer = xPlayer;
        this.oPlayer = oPlayer;
        this.state = state;
    }

    public Game() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public char[] getBoard() {
        return board;
    }

    public void setBoard(char[] board) {
        this.board = board;
    }

    public Player getXPlayer() {
        return xPlayer;
    }

    public void setXPlayer(Player xPlayer) {
        this.xPlayer = xPlayer;
    }

    public Player getOPlayer() {
        return oPlayer;
    }

    public void setOPlayer(Player oPlayer) {
        this.oPlayer = oPlayer;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(id, game.id) &&
                Arrays.equals(board, game.board) &&
                xPlayer.equals(game.xPlayer) &&
                Objects.equals(oPlayer, game.oPlayer) &&
                state == game.state;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, xPlayer, oPlayer, state);
        result = 31 * result + Arrays.hashCode(board);
        return result;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", board=" + Arrays.toString(board) +
                ", xPlayer=" + xPlayer +
                ", oPlayer=" + oPlayer +
                ", state=" + state +
                '}';
    }
}
