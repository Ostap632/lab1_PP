package com.carservice.exception;

public final class BusyMechanicException extends IllegalStateException {
    public BusyMechanicException(String mechanicName) {
        super("Mechanic '" + mechanicName + "' is already assigned to another in-progress order");
    }
}
