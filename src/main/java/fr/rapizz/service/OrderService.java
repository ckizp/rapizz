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
        boolean hasLateDeliveryCompensation = order.getOrderItems().stream()
                .anyMatch(item -> item.getFreeReason() == FreeReason.LATE_DELIVERY);

        if (hasLateDeliveryCompensation) {
            return 0.0;
        }

        return order.getOrderItems().stream()
                .mapToDouble(item -> {
                    BigDecimal unitPrice = item.getPizzaPrice();
                    int quantity = item.getQuantity();

                    if (item.getFreeReason() == FreeReason.LOYALTY) {
                        if (quantity > 1) {
                            BigDecimal remainingTotal = unitPrice.multiply(new BigDecimal(quantity - 1));
                            return remainingTotal.doubleValue();
                        } else {
                            return 0.0;
                        }
                    } else {
                        return unitPrice.doubleValue() * quantity;
                    }
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
            log.info("Order #{} marked as delivered at {}", orderId, order.getDeliveredAt());

            if (isLateDelivery(order)) {
                log.info("Order #{} was delivered late - applying automatic refund", orderId);
                processLateDeliveryRefund(order);
            }
        }

        return repository.save(order);
    }

    @Transactional
    public void processLateDeliveryRefund(Order order) {
        BigDecimal refundAmount = BigDecimal.ZERO;

        Client client = clientService.findById(order.getClient().getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        log.info("=== LATE DELIVERY REFUND DEBUG ===");
        log.info("Client #{} initial balance: {}€, loyalty: {}",
                client.getClientId(), client.getAmount(), client.getLoyaltyCounter());

        for (OrderPizza orderPizza : order.getOrderItems()) {
            BigDecimal unitPrice = orderPizza.getPizzaPrice();
            int quantity = orderPizza.getQuantity();

            log.info("Processing pizza: {} x{}, price: {}€, freeReason: {}",
                    orderPizza.getPizza().getPizzaName(), quantity, unitPrice, orderPizza.getFreeReason());

            if (orderPizza.getFreeReason() == FreeReason.NOT_FREE) {
                BigDecimal pizzaRefund = unitPrice.multiply(new BigDecimal(quantity));
                refundAmount = refundAmount.add(pizzaRefund);
                log.info("NOT_FREE pizza refund: {}€", pizzaRefund);
            } else if (orderPizza.getFreeReason() == FreeReason.LOYALTY) {
                if (quantity > 1) {
                    BigDecimal paidAmount = unitPrice.multiply(new BigDecimal(quantity - 1));
                    refundAmount = refundAmount.add(paidAmount);
                    log.info("LOYALTY pizza refund (paid portion): {}€", paidAmount);
                } else {
                    log.info("LOYALTY pizza fully free, no money refund");
                }
            }
        }

        int loyaltyPointsToRefund = 0;
        for (OrderPizza orderPizza : order.getOrderItems()) {
            if (orderPizza.getFreeReason() == FreeReason.LOYALTY) {
                loyaltyPointsToRefund += 10;
            }
        }

        log.info("Total money refund calculated: {}€", refundAmount);
        log.info("Total loyalty points to refund: {}", loyaltyPointsToRefund);

        if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            for (OrderPizza orderPizza : order.getOrderItems()) {
                orderPizza.setFreeReason(FreeReason.LATE_DELIVERY);
            }

            BigDecimal currentBalance = client.getAmount();
            BigDecimal newAmount = currentBalance.add(refundAmount);

            log.info("Refunding money: current={}€ + refund={}€ = new={}€",
                    currentBalance, refundAmount, newAmount);

            clientService.updateAmount(client.getClientId(), newAmount);

            Client verificationClient = clientService.findById(client.getClientId()).orElse(null);
            if (verificationClient != null) {
                log.info("Client balance after update in DB: {}€", verificationClient.getAmount());

                if (verificationClient.getAmount().compareTo(newAmount) != 0) {
                    log.error("Balance update failed! Expected: {}€, Actual: {}€",
                            newAmount, verificationClient.getAmount());

                    verificationClient.setAmount(newAmount);
                    clientService.save(verificationClient);

                    Client finalCheck = clientService.findById(client.getClientId()).orElse(null);
                    if (finalCheck != null) {
                        log.info("Final balance check: {}€", finalCheck.getAmount());
                    }
                }
            }

            log.info("Client #{} refunded {}€ for late delivery. New balance: {}€",
                    client.getClientId(), refundAmount.doubleValue(), newAmount.doubleValue());
        }

        if (loyaltyPointsToRefund > 0) {
            int currentPoints = client.getLoyaltyCounter();
            int newPoints = currentPoints + loyaltyPointsToRefund;
            clientService.updateLoyaltyCounter(client.getClientId(), newPoints);

            log.info("Client #{} refunded {} loyalty points for late delivery. Points: {} -> {}",
                    client.getClientId(), loyaltyPointsToRefund, currentPoints, newPoints);
        }
    }
} 