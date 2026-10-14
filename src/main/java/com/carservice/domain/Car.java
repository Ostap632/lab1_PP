package com.carservice.domain;

import java.util.Locale;
import java.util.Objects;

public final class Car {
    private final String vin;
    private final String make;
    private final String model;
    private final int year;
    private final Client owner;

    public Car(String vin, String make, String model, int year, Client owner) {
        String normalizedVin = requireText(vin, "VIN").toUpperCase(Locale.ROOT);
        String checkedMake = requireText(make, "make");
        String checkedModel = requireText(model, "model");
        if (year <= 0) {
            throw new IllegalArgumentException("year must be positive");
        }
        Client checkedOwner = Objects.requireNonNull(owner, "owner must not be null");

        this.vin = normalizedVin;
        this.make = checkedMake;
        this.model = checkedModel;
        this.year = year;
        this.owner = checkedOwner;
    }

    public String getVin() {
        return vin;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public Client getOwner() {
        return owner;
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be empty");
        }
        return trimmed;
    }
}
