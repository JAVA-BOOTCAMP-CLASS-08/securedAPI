package com.sicos.secured.exceptions;

import com.sicos.secured.exceptions.security.ClientBasicFaultException;
import com.sicos.secured.exceptions.security.ClientException;
import com.sicos.secured.exceptions.security.UnauthorizedException;
import com.sicos.secured.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({RepositoryError.class})
    public ResponseEntity<Object> handleInternalError(RuntimeException exception, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()));
    }

    @ExceptionHandler({ClientBasicFaultException.class})
    public ResponseEntity<Object> handleBasicAuthorizationError(RuntimeException exception, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .header("WWW-Authenticate", "Basic realm='Secured'")
                .build();
    }

    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<Object> handleUnauthorizedException(RuntimeException exception, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), exception.getMessage()));
    }

    @ExceptionHandler({ClientException.class})
    public ResponseEntity<Object> handleBadRequestException(RuntimeException exception, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage()));
    }
    @ExceptionHandler(value = {RolAlreadyAssignedException.class,
                                RolAlreadyCreatedException.class,
                                UserAlreadyCreatedException.class})
    public ResponseEntity<Object> handleAlreadyReported(RuntimeException exception, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.ALREADY_REPORTED)
                .body(new ErrorResponse(HttpStatus.ALREADY_REPORTED.value(), exception.getMessage()));
    }

    @ExceptionHandler(value = {RolNotAssignedException.class, RolNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<Object> handleNotFound(RuntimeException exception, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage()));
    }

}
