package pl.itacademy.tictactoe.api;

import pl.itacademy.tictactoe.domain.*;
import pl.itacademy.tictactoe.exception.GameNotFoundException;
import pl.itacademy.tictactoe.exception.IllegalMoveException;

import java.util.Optional;

public class GameService implements GameInterface {
    private final GameRepository repository;

    public GameService(GameRepository repository) {
        this.repository = repository;
    }

    @Override
    public GameResponse registerPlayer(Player player) {
        Optional<Game> waitingGame = repository.getWaitingGame();
        Game game;
        if (waitingGame.isEmpty()) {
            game = new Game();
            game.setXPlayer(player);
            game.setState(GameState.WAITING_FOR_REGISTRATION);
            repository.addGame(game);
        } else {
            game = waitingGame.get();
            game.setOPlayer(player);
            game.setState(GameState.X_MOVE);
        }
        return GameResponse.from(game);
    }

    @Override
    public GameResponse makeMove(Move move) {
        Game game = repository.getGameById(move.getGameId()).orElseThrow(() -> new GameNotFoundException("Game [" + move.getGameId() + "] not found"));
        if(game.getState()==GameState.O_WON||game.getState()==GameState.X_WON||game.getState()==GameState.DRAW){
            throw new IllegalMoveException("It is not possible to make move on cell ["+move.getCellIndex()+"] because game is already finished");
        }
        if((move.getPlayer().equals(game.getXPlayer())&&game.getState()==GameState.O_MOVE)||(move.getPlayer().equals(game.getOPlayer())&&game.getState()==GameState.X_MOVE)){
            throw new IllegalMoveException("Player ["+move.getPlayer().getName()+"] should wait for his turn");
        }


        game.setState(GameState.O_MOVE);



        if(game.getBoard()[move.getCellIndex()]=='X'||game.getBoard()[move.getCellIndex()]=='O'){
            throw new IllegalMoveException("It is not possible to make move on cell ["+move.getCellIndex()+"] because it is not empty");
        }


        game.getBoard()[move.getCellIndex()] = 'X';
        return GameResponse.from(game);


    }

    @Override
    public GameResponse getGameState(int gameId) {
        Game game = repository.getGameById(gameId).orElseThrow(() -> new GameNotFoundException("Game [" + gameId + "] not found"));
        return GameResponse.from(game);
    }

    @Override
    public GameResponse playAgain(int gameId) {
        return null;
    }

    @Override
    public GameStatistics getGameStatistic(Player player) {
        return null;
    }
}