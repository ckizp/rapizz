package fr.rapizz.controller;

import fr.rapizz.model.*;
import fr.rapizz.service.*;
import fr.rapizz.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryController {
    private final OrderService orderService;
    private final DeliveryDriverService driverService;
    private final VehicleService vehicleService;
    private final PizzaService pizzaService;
    private final ClientService clientService;

    public List<DeliveryDriver> getAllDrivers() {
        return driverService.findAll();
    }

    public List<DeliveryDriver> getAvailableDrivers() {
        return driverService.findAvailableDrivers();
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleService.findAll();
    }

    public List<Vehicle> getAvailableVehicles() {
        return vehicleService.findAvailableVehicles();
    }

    public List<Pizza> getAllPizzas() {
        return pizzaService.findAll();
    }

    public List<Order> getActiveOrders() {
        return orderService.findActiveOrders();
    }

    public BigDecimal calculatePizzaPrice(Pizza pizza, PizzaSize size) {
        return pizzaService.calculatePrice(pizza, size);
    }

    public Result<Order> createDelivery(DeliveryDriver driver, Vehicle vehicle, Client client,
                                        List<OrderPizza> pizzas, String paymentMethod,
                                        List<Integer> freePizzaIndices) {
        log.debug("Creating delivery for client: {}, driver: {}, vehicle: {}, payment: {}",
                client.getClientId(), driver.getDriverId(), vehicle.getVehicleId(), paymentMethod);

        if (pizzas.isEmpty()) {
            return Result.failure("Au moins une pizza doit être sélectionnée");
        }

        try {
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (int i = 0; i < pizzas.size(); i++) {
                OrderPizza pizza = pizzas.get(i);
                BigDecimal unitPrice = pizza.getPizzaPrice();
                int quantity = pizza.getQuantity();

                if (freePizzaIndices.contains(i)) {
                    if (quantity > 1) {
                        BigDecimal remainingTotal = unitPrice.multiply(new BigDecimal(quantity - 1));
                        totalAmount = totalAmount.add(remainingTotal);
                    }
                } else {
                    BigDecimal pizzaTotal = unitPrice.multiply(new BigDecimal(quantity));
                    totalAmount = totalAmount.add(pizzaTotal);
                }
            }

            if (client.getAmount().compareTo(totalAmount) < 0) {
                return Result.failure("Solde insuffisant. Solde: " + client.getAmount() + "€, Total: " + totalAmount + "€");
            }

            int requiredPoints = freePizzaIndices.size() * 10;
            if (client.getLoyaltyCounter() < requiredPoints) {
                return Result.failure("Points de fidélité insuffisants. Points: " + client.getLoyaltyCounter() + ", Requis: " + requiredPoints);
            }

            Order order = new Order();
            order.setDriver(driver);
            order.setVehicle(vehicle);
            order.setClient(client);
            order.setOrderStatus(OrderStatus.PENDING);

            for (int i = 0; i < pizzas.size(); i++) {
                OrderPizza orderPizza = pizzas.get(i);
                OrderPizza newOrderPizza = new OrderPizza();
                newOrderPizza.setPizza(orderPizza.getPizza());
                newOrderPizza.setPizzaSize(orderPizza.getPizzaSize());
                newOrderPizza.setQuantity(orderPizza.getQuantity());
                newOrderPizza.setPizzaPrice(orderPizza.getPizzaPrice());
                newOrderPizza.setOrder(order);

                if (freePizzaIndices.contains(i)) {
                    newOrderPizza.setFreeReason(FreeReason.LOYALTY);
                } else {
                    newOrderPizza.setFreeReason(FreeReason.NOT_FREE);
                }

                order.addOrderItem(newOrderPizza);
            }

            Order savedOrder = orderService.save(order);

            if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal newBalance = client.getAmount().subtract(totalAmount);
                clientService.updateAmount(client.getClientId(), newBalance);
                log.info("Client #{} debited: {}€ - {}€ = {}€",
                        client.getClientId(), client.getAmount(), totalAmount, newBalance);
            }

            if (requiredPoints > 0) {
                int newPoints = client.getLoyaltyCounter() - requiredPoints;
                clientService.updateLoyaltyCounter(client.getClientId(), newPoints);
                log.info("Client #{} loyalty points deducted: {} - {} = {}",
                        client.getClientId(), client.getLoyaltyCounter(), requiredPoints, newPoints);
            }

            int pizzasBought = 0;
            for (int i = 0; i < pizzas.size(); i++) {
                OrderPizza pizza = pizzas.get(i);
                if (freePizzaIndices.contains(i)) {
                    if (pizza.getQuantity() > 1) {
                        pizzasBought += (pizza.getQuantity() - 1);
                    }
                } else {
                    pizzasBought += pizza.getQuantity();
                }
            }

            if (pizzasBought > 0) {
                clientService.incrementLoyaltyCounter(client, pizzasBought);
                log.info("Added {} loyalty points to client #{}", pizzasBought, client.getClientId());
            }

            return Result.success(savedOrder);

        } catch (Exception e) {
            log.error("Error creating delivery", e);
            return Result.failure("Erreur lors de la création de la livraison: " + e.getMessage());
        }
    }

    public Result<Order> updateOrderStatus(Integer orderId, OrderStatus newStatus) {
        try {
            Order updated = orderService.updateStatus(orderId, newStatus);
            return Result.success(updated);
        } catch (Exception e) {
            log.error("Error updating order status for order #{}", orderId, e);
            return Result.failure("Erreur lors de la mise à jour du statut: " + e.getMessage());
        }
    }

    public boolean isLateDelivery(Order order) {
        return orderService.isLateDelivery(order);
    }

    public double calculateOrderTotal(Order order) {
        return orderService.calculateOrderTotal(order);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderService.findByStatus(status);
    }

    public List<Order> getAllOrders() {
        return orderService.findAll();
    }
}