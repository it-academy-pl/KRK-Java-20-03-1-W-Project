package pl.itacademy.tictactoe.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.itacademy.tictactoe.domain.ErrorResponse;
import pl.itacademy.tictactoe.exception.GameNotFoundException;
import pl.itacademy.tictactoe.exception.IllegalMoveException;
import pl.itacademy.tictactoe.exception.InvalidPasswordException;
import pl.itacademy.tictactoe.exception.PlayerNotFoundException;

@ControllerAdvice
public class ExceptionHandlerResolver extends ResponseEntityExceptionHandler {

    @ExceptionHandler({GameNotFoundException.class, PlayerNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleGameNotFoundException(Exception exception, WebRequest request) {
        return from(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({InvalidPasswordException.class, IllegalMoveException.class})
    public ResponseEntity<ErrorResponse> handleInvalidPasswordException(Exception exception, WebRequest request) {
        return from(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    private ResponseEntity<ErrorResponse> from(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(message));
    }

}
