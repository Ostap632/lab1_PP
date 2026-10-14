package com.carservice.exception;

public final class DuplicateVinException extends IllegalArgumentException {
    public DuplicateVinException(String vin) {
        super("A car with VIN '" + vin + "' is already registered");
    }
}
