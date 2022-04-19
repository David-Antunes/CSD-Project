package com.csd.bftsmart.exceptions;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class HandleWebExceptions {
    public static <T> T resultOrException(Either<T> result) {
        return switch (result.left()) {
            case SUCCESS -> result.right();
            case USER_EXISTS -> throw new ResponseStatusException(BAD_REQUEST, "User already exists.");
            case USER_DOES_NOT_EXIST -> throw new ResponseStatusException(BAD_REQUEST, "User does not exist.");
            case INVALID_USER -> throw new ResponseStatusException(BAD_REQUEST, "Invalid user.");
            case INVALID_SIGNATURE -> throw new ResponseStatusException(BAD_REQUEST, "Invalid digital signature.");
            case ACCOUNT_DOES_NOT_EXIST -> throw new ResponseStatusException(BAD_REQUEST, "Account does not exist.");
            case ACCOUNT_DOES_NOT_BELONG_TO_USER -> throw new ResponseStatusException(BAD_REQUEST, "Account does not belong to user");
        };
    }
}
