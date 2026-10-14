package com.carservice.exception;

public final class InvalidOrderStateException extends IllegalStateException {
    public InvalidOrderStateException(String message) {
        super(message);
    }
}
