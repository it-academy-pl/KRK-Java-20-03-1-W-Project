package pl.itacademy.tictactoe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pl.itacademy.tictactoe.api.GameInMemoryRepository;
import pl.itacademy.tictactoe.api.GameInterface;
import pl.itacademy.tictactoe.api.GameRepository;
import pl.itacademy.tictactoe.api.GameService;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public GameInterface gameInterface(GameRepository repository) {
        return new GameService(repository);
    }

    @Bean
    public GameRepository gameRepository() {
        return new GameInMemoryRepository();
    }
}
