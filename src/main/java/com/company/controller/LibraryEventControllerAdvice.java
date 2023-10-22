package com.company.controller;

import com.company.exception.BadRequestException;
import com.company.exception.LibraryEventProducerException;
import com.company.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class LibraryEventControllerAdvice {

    private static final String METHOD_ARGUMENT_NOT_VALID_CODE = "argument.not.valid";
    private static final String INTERNAL_SERVER_ERROR_CODE = "internal.server.error";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRequestBody(MethodArgumentNotValidException ex) {

        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + "-" + fieldError.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining(", "));

        return handle(errorMessage, METHOD_ARGUMENT_NOT_VALID_CODE);

    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(BadRequestException e) {
        return handle(e.getMessage(), e.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(Exception e) {
        return handle(e.getMessage(), INTERNAL_SERVER_ERROR_CODE);
    }

    @ExceptionHandler(LibraryEventProducerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleLibraryEventProducerException(LibraryEventProducerException e) {

        log.error(e.getMessage());
        return handle(e.getMessage(), e.getErrorCode());
    }

    private ErrorResponse handle(String message, String code) {
        return new ErrorResponse(code, message);
    }
}
