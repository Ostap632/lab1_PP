package com.carservice.domain;

import java.math.BigDecimal;
import java.util.Objects;

public final class Job {
    private final String description;
    private final BigDecimal partsCost;
    private final BigDecimal laborCost;
    private boolean completed;

    public Job(String description, BigDecimal partsCost, BigDecimal laborCost) {
        this.description = requireText(description, "description");
        this.partsCost = requireCost(partsCost, "partsCost");
        this.laborCost = requireCost(laborCost, "laborCost");
        this.completed = false;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPartsCost() {
        return partsCost;
    }

    public BigDecimal getLaborCost() {
        return laborCost;
    }

    public boolean isCompleted() {
        return completed;
    }

    public BigDecimal getTotalCost() {
        return partsCost.add(laborCost);
    }

    public void complete() {
        if (completed) {
            throw new IllegalStateException("Job is already completed");
        }
        completed = true;
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be empty");
        }
        return trimmed;
    }

    private static BigDecimal requireCost(BigDecimal value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        if (value.signum() < 0) {
            throw new IllegalArgumentException(fieldName + " must not be negative");
        }
        return value;
    }
}
