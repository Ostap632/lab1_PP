package com.carservice;

import java.math.BigDecimal;
import java.util.List;

import com.carservice.domain.Car;
import com.carservice.domain.Client;
import com.carservice.domain.Job;
import com.carservice.domain.Mechanic;
import com.carservice.domain.Order;
import com.carservice.service.CarService;

public final class CarServiceDemo {
    private CarServiceDemo() {
    }

    public static void main(String[] args) {
        CarService service = new CarService();
        Client client = new Client("Jordan Lee");
        Car car = new Car("DEMO0000000000001", "Toyota", "Corolla", 2021, client);
        Mechanic mechanic = new Mechanic("Sam Rivera", "Engine repair");

        service.registerClient(client);
        service.registerCar(car);
        service.registerMechanic(mechanic);

        Order completedOrder = new Order(car, List.of());
        completedOrder.addJob(new Job("Replace oil filter", new BigDecimal("18.50"), new BigDecimal("25.00")));
        completedOrder.addJob(new Job("Inspect brakes", BigDecimal.ZERO, new BigDecimal("35.00")));
        service.registerOrder(completedOrder);
        completedOrder.diagnose();
        completedOrder.approve();
        service.startOrder(completedOrder, mechanic);
        for (Job job : completedOrder.getJobs()) {
            job.complete();
        }
        completedOrder.complete();
        System.out.println("Completed order: " + completedOrder.getStatus()
                + ", total price: " + completedOrder.getTotalPrice());

        Order emptyOrder = new Order(car, List.of());
        service.registerOrder(emptyOrder);
        emptyOrder.diagnose();
        runScenario("Approve an empty order", emptyOrder::approve);

        Order unassignedOrder = createApprovedOrder(service, car,
                new Job("Replace air filter", new BigDecimal("12.00"), new BigDecimal("15.00")));
        runScenario("Start work without an assigned mechanic", unassignedOrder::startWork);

        Order activeOrder = createApprovedOrder(service, car,
                new Job("Check battery", BigDecimal.ZERO, new BigDecimal("20.00")));
        service.startOrder(activeOrder, mechanic);

        Order competingOrder = createApprovedOrder(service, car,
                new Job("Rotate tires", BigDecimal.ZERO, new BigDecimal("30.00")));
        runScenario("Assign a mechanic who is already busy",
                () -> service.assignMechanicToOrder(competingOrder, mechanic));

        runScenario("Modify a completed order", completedOrder::diagnose);

        Order invalidTransitionOrder = new Order(car, List.of(
                new Job("Test transition", BigDecimal.ZERO, new BigDecimal("10.00"))
        ));
        service.registerOrder(invalidTransitionOrder);
        runScenario("Attempt invalid status transition", invalidTransitionOrder::complete);
    }

    private static Order createApprovedOrder(CarService service, Car car, Job job) {
        Order order = new Order(car, List.of(job));
        service.registerOrder(order);
        order.diagnose();
        order.approve();
        return order;
    }

    private static void runScenario(String label, Runnable action) {
        try {
            action.run();
        } catch (RuntimeException exception) {
            System.out.println(label + " rejected: " + exception.getMessage());
        }
    }
}
