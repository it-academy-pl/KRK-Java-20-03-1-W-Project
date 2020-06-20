package pl.itacademy.tictactoe.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pl.itacademy.tictactoe.domain.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameControllerTest {

    private RestTemplate restTemplate;

    @Autowired
    private GameRepository gameRepository;

    @LocalServerPort
    private int port;

    private String uriPrefix;

    @BeforeEach
    public void setUp() {
        restTemplate = new RestTemplate();
        uriPrefix = "http://localhost:" + port + "/game";
    }

    @AfterEach
    public void tearDown() {
        gameRepository.games().clear();
    }

    @Test
    public void registerPlayer_noWaitingGames_createsGameAndRegistersPlayer() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<Player> request = new HttpEntity<>(new Player("JanK", "1234"), headers);

        ResponseEntity<GameResponse> response = restTemplate.postForEntity(uriPrefix + "/register", request, GameResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        GameResponse gameResponse = response.getBody();
        assertThat(gameResponse).isNotNull();
        assertThat(gameResponse.getState()).isEqualTo(GameState.WAITING_FOR_REGISTRATION);

        assertThat(gameRepository.getGameById(gameResponse.getGameId())).isPresent();
    }

    @Test
    public void getGameState_gameExists_returnsGameState() {
        Player xPlayer = new Player("x", "pass");
        Player oPlayer = new Player("o", "pass");
        gameRepository.addGame(new Game(xPlayer, oPlayer, GameState.X_MOVE));

        ResponseEntity<GameResponse> response = restTemplate.getForEntity(uriPrefix + "/0", GameResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        GameResponse gameResponse = response.getBody();
        assertThat(gameResponse).isNotNull();
        assertThat(gameResponse.getState()).isEqualTo(GameState.X_MOVE);
    }

    @Test
    public void getGameState_gameNotExists_returnsNotFoundResponse() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> restTemplate.getForEntity(uriPrefix + "/0", ErrorResponse.class));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getMessage()).contains("Game [0] not found");
    }

    @Test
    public void makeMove_playerProvideWrongPassword_returnsBadRequestResponse(){

        Player xPlayer = new Player("x", "pass");
        Player oPlayer = new Player("o", "pass");
        Game game = gameRepository.addGame(new Game(xPlayer, oPlayer, GameState.X_MOVE));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        Move move = new Move(game.getId(),0,new Player("x","123"));
        HttpEntity<Move> request = new HttpEntity<Move>(move, headers);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () ->  restTemplate.postForEntity(uriPrefix, request, GameResponse.class));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getMessage()).contains("Player x provided invalid password");

    }

}