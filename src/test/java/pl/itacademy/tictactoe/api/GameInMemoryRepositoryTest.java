package pl.itacademy.tictactoe.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.itacademy.tictactoe.domain.Game;
import pl.itacademy.tictactoe.domain.GameState;
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
        repository.addGame(game1);
        Integer id = game1.getId();
        Game updated = new Game();
        updated.setId(id);

        repository.updateGame(updated);
        assertThat(repository.updateGame(updated)).isEqualTo(updated);
        assertThat(repository.games()).contains(updated);
    }

    @Test
    public void updateGame_nonExistingGame_throwGameNotFoundException() {
        game1.setId(1);
        repository.addGame(game1);
        Game updated = new Game();
        updated.setId(2);

        GameNotFoundException exception = assertThrows(GameNotFoundException.class, () -> repository.updateGame(updated));
        assertThat(exception.getMessage()).contains("2");

        assertThat(repository.games()).contains(game1);
        assertThat(repository.games()).doesNotContain(updated);
    }

    @Test
    public void getWaitingGame_returnsWaitingGame() {
        Game game = new Game();
        game.setState(GameState.WAITING_FOR_REGISTRATION);
        repository.addGame(game);

        Game waitingGame = repository.getWaitingGame().orElseThrow();
        assertThat(waitingGame).isSameAs(game);
    }

}