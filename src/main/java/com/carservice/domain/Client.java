package com.carservice.domain;

import java.util.Objects;

public final class Client {
    private final String name;

    public Client(String name) {
        this.name = requireText(name, "name");
    }

    public String getName() {
        return name;
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
