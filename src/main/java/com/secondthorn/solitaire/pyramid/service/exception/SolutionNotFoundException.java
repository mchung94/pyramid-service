package com.secondthorn.solitaire.pyramid.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Throw this when a solution doesn't exist and we want to return HTTP status
 * 404 (Not Found)
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class SolutionNotFoundException extends RuntimeException {
    public SolutionNotFoundException(String message) {
        super(message);
    }
}
