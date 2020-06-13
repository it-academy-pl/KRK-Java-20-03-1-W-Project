package pl.itacademy.tictactoe.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.itacademy.tictactoe.domain.Game;
import pl.itacademy.tictactoe.exception.GameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameInMemoryRepositoryTest {

    private GameInMemoryRepository repository;
    private Game game1;

    @BeforeEach
    public void setUp() {
        repository = new GameInMemoryRepository();
        game1 = new Game();
    }

    @Test
    public void addGame_newGame_returnsNewGame() {
        assertThat(repository.addGame(game1)).isEqualTo(game1);
        assertThat(game1.getId()).isNotNull();
    }

    @Test
    public void addGame_newGame_repositoryContainsNewGame() {

        assertThat(repository.games()).doesNotContain(game1);
        repository.addGame(game1);
        assertThat(repository.games()).contains(game1);
    }

    @Test
    public void getGameById_existingId_returnsOptionalWithValue() {
        game1.setId(1);
        repository.addGame(game1);
        Optional<Game> game1 = repository.getGameById(1);
        assertThat(repository.getGameById(1)).isNotEmpty();
    }

    @Test
    public void getGameById_nonExistingId_returnsEmptyOptional() {
        Optional<Game> game = repository.getGameById(123);

        assertThat(game).isEmpty();
    }

    @Test
    public void updateGame_existingGame_returnOptionalWithValue() {
        game1.setId(1);
        repository.addGame(game1);
        Game updated = new Game();
        updated.setId(1);
        char[] board = new char[]{'X', 'X', 'X', 'O', 'X', 'X', 'X', 'X', 'O'};
        updated.setBoard(board);
        repository.updateGame(updated);
        assertThat(repository.updateGame(updated)).isEqualTo(updated);
        assertThat(repository.games()).doesNotContain(game1);
        assertThat(repository.games().contains(updated));
    }

    @Test
    public void updateGame_nonExistingGame_throwGameNotFoundException() {
        game1.setId(1);
        repository.addGame(game1);
        Game updated = new Game();
        updated.setId(2);
        char[] board = new char[]{'X', 'X', 'X', 'O', 'X', 'X', 'X', 'X', 'O'};
        updated.setBoard(board);
        GameNotFoundException exception = assertThrows(GameNotFoundException.class, () -> repository.updateGame(updated));
        assertThat(exception.getMessage()).contains("2");

        assertThat(repository.games()).contains(game1);
        assertThat(repository.games()).doesNotContain(updated);
    }

}