package fr.rapizz.controller;

import fr.rapizz.model.Order;
import fr.rapizz.model.OrderStatus;
import fr.rapizz.service.OrderService;
import fr.rapizz.service.ValidationService;
import fr.rapizz.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService service;
    private final ValidationService validator;

    public List<Order> getAllOrders() {
        return service.findAll();
    }

    public Optional<Order> getOrderById(Integer id) {
        return service.findById(id);
    }

    public List<Order> getOrdersByClientId(Integer clientId) {
        return service.findByClientId(clientId);
    }

    public List<Order> getOrdersByDriverId(Integer driverId) {
        return service.findByDriverId(driverId);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return service.findByStatus(status);
    }

    public List<Order> getOrdersByDateRange(LocalDateTime start, LocalDateTime end) {
        return service.findByDateRange(start, end);
    }

    public Result<Order> createOrder(Integer clientId, Integer driverId, Integer vehicleId) {
        log.debug("Creating order for client: {}, driver: {}, vehicle: {}", clientId, driverId, vehicleId);

        Order order = new Order();
        // Other fields will be initialized by @PrePersist

        // Order validation
        List<String> errors = validator.validateEntity(order);
        if (!errors.isEmpty()) {
            return Result.failure(errors);
        }

        try {
            Order saved = service.save(order);
            return Result.success(saved);
        } catch (Exception e) {
            return Result.failure("Error while creating order: " + e.getMessage());
        }
    }

    public Result<Order> updateOrderStatus(Integer orderId, OrderStatus newStatus) {
        try {
            Order updated = service.updateStatus(orderId, newStatus);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.failure("Erreur lors de la mise à jour du statut: " + e.getMessage());
        }
    }

    public Result<Order> updateOrderRating(Integer orderId, Integer rating) {
        try {
            Order updated = service.updateRating(orderId, rating);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.failure("Erreur lors de la mise à jour de la note: " + e.getMessage());
        }
    }

    public Result<Void> deleteOrder(Integer id) {
        try {
            service.delete(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.failure("Erreur lors de la suppression de la commande: " + e.getMessage());
        }
    }
} 