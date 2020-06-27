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
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import pl.itacademy.tictactoe.domain.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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
        restTemplate.setErrorHandler(new NoErrorResponseHandler());

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
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(uriPrefix + "/0", ErrorResponse.class);

        ErrorResponse error = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(error).isNotNull();
        assertThat(error.getMessage()).contains("Game [0] not found");
    }

    @Test
    public void makeMove_playerProvideWrongPassword_returnsBadRequestResponse() {

        Player xPlayer = new Player("x", "pass");
        Player oPlayer = new Player("o", "pass");
        Game game = gameRepository.addGame(new Game(xPlayer, oPlayer, GameState.X_MOVE));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        Move move = new Move(game.getId(), 0, new Player("x", "123"));
        HttpEntity<Move> request = new HttpEntity<>(move, headers);

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(uriPrefix, request, ErrorResponse.class);

        ErrorResponse error = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(error).isNotNull();
        assertThat(error.getMessage()).contains("Player x provided invalid password");
    }

    @Test
    public void makeMove_properMove_returnsGameResponseWithPerformedMove() {
        Player xPlayer = new Player("x", "pass");
        Player oPlayer = new Player("o", "pass");
        Game game = gameRepository.addGame(new Game(xPlayer, oPlayer, GameState.X_MOVE));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        Move move = new Move(game.getId(), 0, new Player("x", "pass"));
        HttpEntity<Move> request = new HttpEntity<>(move, headers);

        ResponseEntity<GameResponse> response = restTemplate.postForEntity(uriPrefix, request, GameResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        GameResponse gameResponse = response.getBody();
        assertThat(gameResponse).isNotNull();
        assertThat(gameResponse.getBoard()[0]).isEqualTo('X');
        assertThat(gameResponse.getState()).isEqualTo(GameState.O_MOVE);
    }

    @Test
    public void makeMove_illegalMove_returnsBadRequestResponse() {
        Player xPlayer = new Player("x", "pass");
        Player oPlayer = new Player("o", "pass");
        Game game = gameRepository.addGame(new Game(xPlayer, oPlayer, GameState.X_MOVE));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        Move moveX = new Move(game.getId(), 0, new Player("x", "pass"));
        HttpEntity<Move> request = new HttpEntity<>(moveX, headers);
        restTemplate.postForEntity(uriPrefix, request, GameResponse.class);

        Move moveO = new Move(game.getId(), 0, new Player("o", "pass"));
        HttpEntity<Move> requestO = new HttpEntity<>(moveO, headers);
        ResponseEntity<ErrorResponse> responseO = restTemplate.postForEntity(uriPrefix, requestO, ErrorResponse.class);

        ErrorResponse error = responseO.getBody();
        assertThat(responseO.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(error).isNotNull();
        assertThat(error.getMessage()).contains("Board cell 0 already contains value X");

    }

    @Test
    public void makeMove_wrongPlayer_returnsNotFoundResponse() {
        Player xPlayer = new Player("x", "pass");
        Player oPlayer = new Player("o", "pass");
        Game game = gameRepository.addGame(new Game(xPlayer, oPlayer, GameState.X_MOVE));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        Move move = new Move(game.getId(), 0, new Player("wrongPlayer", "pass"));
        HttpEntity<Move> request = new HttpEntity<>(move, headers);

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(uriPrefix, request, ErrorResponse.class);

        ErrorResponse error = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(error).isNotNull();
        assertThat(error.getMessage()).contains("Player wrongPlayer not found in game [1]");
    }

    @Test
    public void playAgain_gameNotFound_returnsNotFoundResponse() {
        Player xPlayer = new Player("x", "pass");
        Player oPlayer = new Player("o", "pass");
        Game game = gameRepository.addGame(new Game(xPlayer, oPlayer, GameState.DRAW));

        int gameId = game.getId() + 1;

        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(uriPrefix + "/again/" + gameId, ErrorResponse.class);

        ErrorResponse error = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(error).isNotNull();
        assertThat(error.getMessage()).contains("Game [" + gameId + "] not found");
    }

    @Test
    public void playAgain_gameFound_returnsGameResponseWithNewGame() {

        Player xPlayer = new Player("x", "pass");
        Player oPlayer = new Player("o", "pass");
        Game game = gameRepository.addGame(new Game(xPlayer, oPlayer, GameState.DRAW));

        int gameId = game.getId();

        ResponseEntity<GameResponse> response = restTemplate.getForEntity(uriPrefix + "/again/" + gameId, GameResponse.class);

        GameResponse gameResponse = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        System.out.println(gameResponse);
        assertThat(gameResponse).isNotNull();
        assertThat(gameResponse.getGameId()).isEqualTo(gameId + 1);
        assertThat(gameResponse.getState()).isEqualTo(GameState.X_MOVE);

        Optional<Game> newGame = gameRepository.getGameById(gameId + 1);
        assertThat(newGame).isPresent();
        assertThat(newGame.get().getXPlayer()).isEqualTo(oPlayer);
        assertThat(newGame.get().getOPlayer()).isEqualTo(xPlayer);
    }

    @Test
    public void getGameStatistic_playerNotFound_returnsNotFoundResponse() {
        Player xPlayer = new Player("x", "pass");
        Player oPlayer = new Player("o", "pass");
        gameRepository.addGame(new Game(xPlayer, oPlayer, GameState.DRAW));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<Player> request = new HttpEntity<>(new Player("JanK", "1234"), headers);

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(uriPrefix + "/stats", request, ErrorResponse.class);

        ErrorResponse error = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(error).isNotNull();
        assertThat(error.getMessage()).contains("Player JanK not found");
    }

    @Test
    public void getGameStatistic_playerFound_returnsPlayerStatistic() {
        Player xPlayer = new Player("x", "pass");
        Player oPlayer = new Player("o", "pass");
        gameRepository.addGame(new Game(xPlayer, oPlayer, GameState.DRAW));
        gameRepository.addGame(new Game(xPlayer, oPlayer, GameState.X_WON));
        gameRepository.addGame(new Game(xPlayer, oPlayer, GameState.X_WON));
        gameRepository.addGame(new Game(xPlayer, oPlayer, GameState.O_WON));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<Player> request = new HttpEntity<>(xPlayer, headers);

        ResponseEntity<GameStatistics> response = restTemplate.postForEntity(uriPrefix + "/stats", request, GameStatistics.class);

        GameStatistics statistics = response.getBody();
        assertThat(statistics).isNotNull();
        assertThat(statistics.getDraw()).isEqualTo(1);
        assertThat(statistics.getWon()).isEqualTo(2);
        assertThat(statistics.getLost()).isEqualTo(1);

    }

    private static class NoErrorResponseHandler extends DefaultResponseErrorHandler {
        @Override
        public boolean hasError(ClientHttpResponse response) {
            return false;
        }
    }

}