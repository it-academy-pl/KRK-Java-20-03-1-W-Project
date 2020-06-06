package pl.itacademy.tictactoe.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.itacademy.tictactoe.domain.Game;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class GameInMemoryRepositoryTest {

    private GameInMemoryRepository repository;

    @BeforeEach
    public void setUp() {
        repository = new GameInMemoryRepository();
    }

    @Test
    public void getGameById_nonExistingId_returnsEmptyOptional() {
        Optional<Game> game = repository.getGameById(123);

        assertThat(game).isEmpty();
    }

}