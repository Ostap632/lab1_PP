package com.carservice.exception;

public final class InvalidRegistrationException extends IllegalArgumentException {
    public InvalidRegistrationException(String message) {
        super(message);
    }
}
