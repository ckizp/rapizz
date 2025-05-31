package fr.rapizz.service;

import fr.rapizz.model.Order;
import fr.rapizz.model.OrderStatus;
import fr.rapizz.model.OrderPizza;
import fr.rapizz.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository repository;
    private static final int DELIVERY_TIME_THRESHOLD_MINUTES = 45;
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    public List<Order> findAll() {
        return repository.findAll();
    }

    public Optional<Order> findById(Integer id) {
        return repository.findById(id);
    }

    public List<Order> findByClientId(Integer clientId) {
        return repository.findByClient_ClientId(clientId);
    }

    public List<Order> findByDriverId(Integer driverId) {
        return repository.findByDriver_driverId(driverId);
    }

    public List<Order> findByStatus(OrderStatus status) {
        return repository.findByOrderStatus(status);
    }

    public List<Order> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return repository.findByOrderDateBetween(start, end);
    }

    public List<Order> findActiveOrders() {
        List<Order> pendingOrders = repository.findByOrderStatusWithDetails(OrderStatus.PENDING);
        List<Order> inProgressOrders = repository.findByOrderStatusWithDetails(OrderStatus.IN_PROGRESS);
        pendingOrders.addAll(inProgressOrders);
        return pendingOrders;
    }

    public String calculateDeliveryTime(Order order) {
        // Logique de calcul du temps de livraison
        // On pourrait prendre en compte :
        // - La distance
        // - Le type de véhicule
        // - Le trafic
        // - L'heure de la journée
        // Pour l'instant, on retourne une valeur fixe
        return "30 minutes";
    }

    public boolean isLateDelivery(Order order) {
        return Duration.between(order.getOrderDate(), LocalDateTime.now())
                .toMinutes() > DELIVERY_TIME_THRESHOLD_MINUTES;
    }

    public String formatOrderDetails(Order order) {
        return order.getOrderItems().stream()
                .map(item -> item.getPizza().getPizzaName() + " - " + item.getPizzaSize())
                .collect(Collectors.joining(", "));
    }

    public double calculateOrderTotal(Order order) {
        return order.getOrderItems().stream()
                .mapToDouble(item -> item.getPizzaPrice().doubleValue() * item.getQuantity())
                .sum();
    }

    @Transactional
    public Order save(Order order) {
        log.info("Début de la sauvegarde de la commande avec {} pizzas", order.getOrderItems().size());
        log.info("Détails des pizzas avant sauvegarde:");
        for (OrderPizza pizza : order.getOrderItems()) {
            log.info("- {} (taille: {}, quantité: {}, prix: {})", 
                pizza.getPizza().getPizzaName(),
                pizza.getPizzaSize(),
                pizza.getQuantity(),
                pizza.getPizzaPrice());
        }

        Order savedOrder = repository.save(order);
        
        log.info("Commande sauvegardée avec l'ID {} et {} pizzas", 
            savedOrder.getOrderId(), savedOrder.getOrderItems().size());
        log.info("Détails des pizzas après sauvegarde:");
        for (OrderPizza pizza : savedOrder.getOrderItems()) {
            log.info("- {} (taille: {}, quantité: {}, prix: {})", 
                pizza.getPizza().getPizzaName(),
                pizza.getPizzaSize(),
                pizza.getQuantity(),
                pizza.getPizzaPrice());
        }
        
        return savedOrder;
    }

    @Transactional
    public Order updateStatus(Integer orderId, OrderStatus newStatus) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
        order.setOrderStatus(newStatus);
        return repository.save(order);
    }

    @Transactional
    public Order updateRating(Integer orderId, Integer rating) {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("La note doit être comprise entre 0 et 5");
        }
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
        order.setClientRating(rating);
        return repository.save(order);
    }

    @Transactional
    public void delete(Integer id) {
        repository.deleteById(id);
    }
} 