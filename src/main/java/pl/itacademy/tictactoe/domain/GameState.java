package pl.itacademy.tictactoe.domain;

import java.util.Optional;

public enum GameState {
    X_WON(null),
    O_WON(null),
    DRAW(null),
    X_MOVE('X'),
    O_MOVE('O'),
    WAITING_FOR_REGISTRATION(null);

    private Character moveChar;

    GameState(Character moveChar) {
        this.moveChar = moveChar;
    }

    public Optional<Character> getMoveChar() {
        return Optional.ofNullable(moveChar);
    }
}
