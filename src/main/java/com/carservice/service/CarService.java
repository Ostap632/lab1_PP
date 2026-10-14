package com.carservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.carservice.domain.Car;
import com.carservice.domain.Client;
import com.carservice.domain.Mechanic;
import com.carservice.domain.Order;
import com.carservice.domain.OrderStatus;
import com.carservice.exception.BusyMechanicException;
import com.carservice.exception.DuplicateVinException;
import com.carservice.exception.InvalidRegistrationException;

public final class CarService {
    private final List<Client> clients = new ArrayList<>();
    private final List<Car> cars = new ArrayList<>();
    private final List<Mechanic> mechanics = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();

    public void registerClient(Client client) {
        addUnique(clients, client, "client");
    }

    public void registerCar(Car car) {
        Objects.requireNonNull(car, "car must not be null");
        for (Car registeredCar : cars) {
            if (registeredCar.getVin().equalsIgnoreCase(car.getVin())) {
                throw new DuplicateVinException(car.getVin());
            }
        }
        cars.add(car);
    }

    public void registerMechanic(Mechanic mechanic) {
        addUnique(mechanics, mechanic, "mechanic");
    }

    public void registerOrder(Order order) {
        Objects.requireNonNull(order, "order must not be null");
        if (!cars.contains(order.getCar())) {
            throw new InvalidRegistrationException("Cannot register order: its car must be registered first");
        }
        addUnique(orders, order, "order");
    }

    public void assignMechanicToOrder(Order order, Mechanic mechanic) {
        requireRegistered(orders, order, "order");
        requireRegistered(mechanics, mechanic, "mechanic");
        ensureMechanicAvailable(order, mechanic);
        order.assignMechanic(mechanic);
    }

    /** Assigns a registered mechanic and starts a registered approved order. */
    public void startOrder(Order order, Mechanic mechanic) {
        requireRegistered(orders, order, "order");
        requireRegistered(mechanics, mechanic, "mechanic");
        ensureMechanicAvailable(order, mechanic);
        order.startWork(mechanic);
    }

    public List<Client> getClients() {
        return List.copyOf(clients);
    }

    public List<Car> getCars() {
        return List.copyOf(cars);
    }

    public List<Mechanic> getMechanics() {
        return List.copyOf(mechanics);
    }

    public List<Order> getOrders() {
        return List.copyOf(orders);
    }

    private void ensureMechanicAvailable(Order targetOrder, Mechanic mechanic) {
        for (Order order : orders) {
            if (order != targetOrder
                    && order.getStatus() == OrderStatus.IN_PROGRESS
                    && mechanic.equals(order.getAssignedMechanic())) {
                throw new BusyMechanicException(mechanic.getName());
            }
        }
    }

    private static <T> void addUnique(List<T> registry, T entity, String entityName) {
        Objects.requireNonNull(entity, entityName + " must not be null");
        if (registry.contains(entity)) {
            throw new InvalidRegistrationException(entityName + " is already registered with this service");
        }
        registry.add(entity);
    }

    private static <T> void requireRegistered(List<T> registry, T entity, String entityName) {
        Objects.requireNonNull(entity, entityName + " must not be null");
        if (!registry.contains(entity)) {
            throw new InvalidRegistrationException(
                    "Cannot use " + entityName + ": it must be registered with this service first");
        }
    }
}
