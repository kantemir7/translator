package ru.fintech.translator.exception;

import org.springframework.http.HttpStatus;

public class TranslationException extends RuntimeException {
    private int statusCode;

    public TranslationException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
