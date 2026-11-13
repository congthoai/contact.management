package com.contact.management.server.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.contact.management.domain.ResponseException;
import com.contact.management.server.response.domain.Response;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResponseException.class)
    public ResponseEntity<Response> responseException(final ResponseException exception) {
        return ResponseEntity.status(exception.code)
                .body(new Response(exception.code, exception.data));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> invalidRequest(final MethodArgumentNotValidException exception) {
        final String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("Request validation failed");

        return ResponseEntity.badRequest()
                .body(new Response(HttpStatus.BAD_REQUEST.value(), message));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response> invalidParameter(final MethodArgumentTypeMismatchException exception) {
        final String message = "Parameter " + exception.getName() + " contains wrong value";

        return ResponseEntity.badRequest()
                .body(new Response(HttpStatus.BAD_REQUEST.value(), message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> globalException(final Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"));
    }
}
