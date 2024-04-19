package org.individualproject.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidExcursionDataException extends ResponseStatusException {
    public InvalidExcursionDataException(String errorCode) {
        super(HttpStatus.BAD_REQUEST, errorCode);
    }
}

