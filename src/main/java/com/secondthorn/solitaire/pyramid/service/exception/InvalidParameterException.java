package com.secondthorn.solitaire.pyramid.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Throw this when the request parameters (deck, goal conditions) are invalid.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidParameterException extends RuntimeException {
    public InvalidParameterException(String message) {
        super(message);
    }
}
