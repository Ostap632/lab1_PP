package com.carservice.domain;

import java.util.Objects;

public final class Mechanic {
    private final String name;
    private final String specialization;

    public Mechanic(String name, String specialization) {
        this.name = requireText(name, "name");
        this.specialization = requireText(specialization, "specialization");
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
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
