package com.carservice.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.carservice.exception.InvalidOrderStateException;

public final class Order {
    private final Car car;
    private final List<Job> jobs;
    private Mechanic assignedMechanic;
    private OrderStatus status;

    public Order(Car car, List<Job> jobs) {
        this.car = Objects.requireNonNull(car, "car must not be null");
        Objects.requireNonNull(jobs, "jobs must not be null");
        this.jobs = new ArrayList<>(jobs.size());
        for (Job job : jobs) {
            this.jobs.add(Objects.requireNonNull(job, "jobs must not contain null"));
        }
        this.status = OrderStatus.CREATED;
    }

    public Car getCar() {
        return car;
    }

    public List<Job> getJobs() {
        return List.copyOf(jobs);
    }

    public void addJob(Job job) {
        ensureModifiable();
        requireStatus(OrderStatus.CREATED, "add a job to");
        jobs.add(Objects.requireNonNull(job, "job must not be null"));
    }

    public Mechanic getAssignedMechanic() {
        return assignedMechanic;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        for (Job job : jobs) {
            total = total.add(job.getPartsCost()).add(job.getLaborCost());
        }
        return total;
    }

    public void diagnose() {
        ensureModifiable();
        transitionFrom(OrderStatus.CREATED, OrderStatus.DIAGNOSED);
    }

    public void approve() {
        ensureModifiable();
        requireStatus(OrderStatus.DIAGNOSED, "approve");
        if (jobs.isEmpty()) {
            throw new InvalidOrderStateException("Cannot approve order: at least one job is required");
        }
        status = OrderStatus.APPROVED;
    }

    public void assignMechanic(Mechanic mechanic) {
        ensureModifiable();
        if (status == OrderStatus.IN_PROGRESS) {
            throw new InvalidOrderStateException("Cannot change the assigned mechanic after work has started");
        }
        assignedMechanic = Objects.requireNonNull(mechanic, "mechanic must not be null");
    }

    public void startWork() {
        ensureModifiable();
        requireStatus(OrderStatus.APPROVED, "start work");
        if (assignedMechanic == null) {
            throw new InvalidOrderStateException("Cannot start work: assign a mechanic to the order first");
        }
        status = OrderStatus.IN_PROGRESS;
    }

    public void startWork(Mechanic mechanic) {
        ensureModifiable();
        requireStatus(OrderStatus.APPROVED, "start work");
        Mechanic checkedMechanic = Objects.requireNonNull(mechanic, "mechanic must not be null");
        assignedMechanic = checkedMechanic;
        status = OrderStatus.IN_PROGRESS;
    }

    public void complete() {
        ensureModifiable();
        requireStatus(OrderStatus.IN_PROGRESS, "complete");
        for (Job job : jobs) {
            if (!job.isCompleted()) {
                throw new InvalidOrderStateException(
                        "Cannot complete order: every job must be marked completed first");
            }
        }
        transitionFrom(OrderStatus.IN_PROGRESS, OrderStatus.COMPLETED);
    }

    public void cancel() {
        ensureModifiable();
        if (status != OrderStatus.CREATED
                && status != OrderStatus.DIAGNOSED
                && status != OrderStatus.APPROVED) {
            throw new InvalidOrderStateException(
                    "Cannot cancel order from status " + status + "; cancellation is allowed only from CREATED, DIAGNOSED, or APPROVED");
        }
        status = OrderStatus.CANCELLED;
    }

    private void transitionFrom(OrderStatus expected, OrderStatus next) {
        requireStatus(expected, "transition to " + next);
        status = next;
    }

    private void ensureModifiable() {
        if (status == OrderStatus.COMPLETED || status == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Cannot modify order: its status is terminal (" + status + ")");
        }
    }

    private void requireStatus(OrderStatus expected, String operation) {
        if (status != expected) {
            throw new InvalidOrderStateException(
                    "Cannot " + operation + " order while status is " + status + "; required status is " + expected);
        }
    }
}
