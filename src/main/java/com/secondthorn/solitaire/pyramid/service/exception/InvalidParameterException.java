package com.secondthorn.solitaire.pyramid.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Throw this when the request parameters (deck, goal conditions) are invalid.
 * This is a HTTP status 400 (Bad Request) because that's what happens
 * automatically when a required parameter is missing, although there are
 * other opinions on the best status to return in this situation.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidParameterException extends RuntimeException {
    public InvalidParameterException(String message) {
        super(message);
    }
}
