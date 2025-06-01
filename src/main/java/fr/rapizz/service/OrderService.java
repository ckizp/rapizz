package fr.rapizz.service;

import fr.rapizz.model.Order;
import fr.rapizz.model.OrderStatus;
import fr.rapizz.model.OrderPizza;
import fr.rapizz.model.FreeReason;
 import fr.rapizz.model.Client;
import fr.rapizz.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository repository;
    private final ClientService clientService;

    private static final int PROMISED_DELIVERY_TIME_MINUTES = 30;

    public List<Order> findAll() {
        return repository.findAllWithDetails();
    }

    public List<Order> findByStatus(OrderStatus status) {
        return repository.findByOrderStatusWithDetails(status);
    }

    public List<Order> findActiveOrders() {
        return repository.findActiveOrdersWithDetails();
    }

    public Optional<Order> findById(Integer id) {
        return repository.findById(id);
    }

    public boolean isLateDelivery(Order order) {
        if (order.getOrderStatus() == OrderStatus.DELIVERED && order.getDeliveredAt() != null) {
            long actualMinutes = Duration.between(order.getOrderDate(), order.getDeliveredAt()).toMinutes();
            return actualMinutes > PROMISED_DELIVERY_TIME_MINUTES;
        } else if (order.getOrderStatus() == OrderStatus.IN_PROGRESS || order.getOrderStatus() == OrderStatus.PENDING) {
            long elapsedMinutes = Duration.between(order.getOrderDate(), LocalDateTime.now()).toMinutes();
            return elapsedMinutes > PROMISED_DELIVERY_TIME_MINUTES;
        }
        return false;
    }

    public double calculateOrderTotal(Order order) {
        return order.getOrderItems().stream()
                .mapToDouble(item -> {
                    if (item.isFree()) {
                        return 0.0;
                    }
                    return item.getPizzaPrice().doubleValue() * item.getQuantity();
                })
                .sum();
    }

    public Set<Integer> getOccupiedDriverIds() {
        return findByStatus(OrderStatus.IN_PROGRESS).stream()
                .filter(order -> order.getDriver() != null)
                .map(order -> order.getDriver().getDriverId())
                .collect(Collectors.toSet());
    }

    public Set<Integer> getOccupiedVehicleIds() {
        return findByStatus(OrderStatus.IN_PROGRESS).stream()
                .filter(order -> order.getVehicle() != null)
                .map(order -> order.getVehicle().getVehicleId())
                .collect(Collectors.toSet());
    }

    @Transactional
    public Order save(Order order) {
        log.info("Starting order save process with {} items", order.getOrderItems().size());
        Order savedOrder = repository.save(order);
        log.info("Order saved successfully with ID {} and {} items",
                savedOrder.getOrderId(), savedOrder.getOrderItems().size());
        return savedOrder;
    }

    @Transactional
    public Order updateStatus(Integer orderId, OrderStatus newStatus) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus oldStatus = order.getOrderStatus();
        order.setOrderStatus(newStatus);

        if (newStatus == OrderStatus.DELIVERED && oldStatus != OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
            log.info("Order #{} marked as delivered at {}", orderId, order.getDeliveredAt());

            if (isLateDelivery(order) && !order.hasLateDeliveryCompensation()) {
                log.info("Order #{} was delivered late - applying automatic refund", orderId);
                processLateDeliveryRefund(order);
            }
        }

        return repository.save(order);
    }

    @Transactional
    public void processLateDeliveryRefund(Order order) {
        BigDecimal refundAmount = order.getOrderItems().stream()
                .filter(item -> item.getFreeReason() == FreeReason.NOT_FREE)
                .map(item -> item.getPizzaPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            for (OrderPizza orderPizza : order.getOrderItems()) {
                if (orderPizza.getFreeReason() == FreeReason.NOT_FREE) {
                    orderPizza.setFreeReason(FreeReason.LATE_DELIVERY);
                    log.info("Pizza #{} marked as late delivery compensation: {} ({})",
                            orderPizza.getOrderItemId(),
                            orderPizza.getPizza().getPizzaName(),
                            orderPizza.getPizzaSize().getDisplayName());
                }
            }

            Client client = order.getClient();
            BigDecimal newAmount = client.getAmount().add(refundAmount);
            clientService.updateAmount(client.getClientId(), newAmount);

            log.info("Client #{} refunded {}€ for late delivery. New balance: {}€",
                    client.getClientId(), refundAmount.doubleValue(), newAmount.doubleValue());
        }
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkForLateDeliveries() {
        List<Order> inProgressOrders = findByStatus(OrderStatus.IN_PROGRESS);

        for (Order order : inProgressOrders) {
            if (isLateDelivery(order) && !order.hasLateDeliveryCompensation()) {
                log.info("Order #{} is late and not yet compensated - processing automatic refund", order.getOrderId());
                processLateDeliveryRefund(order);

                repository.save(order);
            }
        }
    }
} 