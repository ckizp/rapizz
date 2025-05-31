package fr.rapizz.service;

import fr.rapizz.model.Order;
import fr.rapizz.model.OrderStatus;
import fr.rapizz.model.OrderPizza;
import fr.rapizz.model.FreeReason;
 import fr.rapizz.model.Client;
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
import org.springframework.scheduling.annotation.Scheduled;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository repository;
    private final ClientService clientService;
    private static final int DELIVERY_TIME_THRESHOLD_MINUTES = 45;
    private static final int LOYALTY_THRESHOLD = 10;
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

        if (order.getClient() != null) {
            // Calculer le nombre total de pizzas dans la commande
            int totalPizzas = order.getOrderItems().stream()
                .mapToInt(OrderPizza::getQuantity)
                .sum();
            
            // Incrémenter le compteur de fidélité du nombre total de pizzas
            clientService.incrementLoyaltyCounter(order.getClient(), totalPizzas);
            
            // Récupérer le client mis à jour pour vérifier le nouveau compteur
            Client updatedClient = clientService.findById(order.getClient().getClientId()).orElseThrow();
            
            // Vérifier si le client a atteint ou dépassé le seuil de fidélité
            if (updatedClient.getLoyaltyCounter() >= LOYALTY_THRESHOLD) {
                log.info("Client {} a atteint le seuil de fidélité ({} points)", 
                    updatedClient.getClientId(), 
                    updatedClient.getLoyaltyCounter());
                
                // Marquer la commande comme gratuite pour fidélité
                order.setFreeReason(FreeReason.LOYALTY);
                
                // Trouver la pizza la moins chère
                OrderPizza cheapestPizza = order.getOrderItems().stream()
                    .min((p1, p2) -> p1.getPizzaPrice().compareTo(p2.getPizzaPrice()))
                    .orElseThrow();
                
                // Si la pizza la moins chère a une quantité > 1
                if (cheapestPizza.getQuantity() > 1) {
                    // Créer une nouvelle entrée pour la pizza gratuite
                    OrderPizza freePizza = new OrderPizza();
                    freePizza.setOrder(order);
                    freePizza.setPizza(cheapestPizza.getPizza());
                    freePizza.setPizzaSize(cheapestPizza.getPizzaSize());
                    freePizza.setQuantity(1);
                    freePizza.setPizzaPrice(java.math.BigDecimal.ZERO);
                    freePizza.setIsFree(true);
                    order.addOrderItem(freePizza);
                    
                    // Décrémenter la quantité de la pizza originale
                    cheapestPizza.setQuantity(cheapestPizza.getQuantity() - 1);
                } else {
                    // Si quantité = 1, mettre simplement à jour la pizza existante
                    cheapestPizza.setPizzaPrice(java.math.BigDecimal.ZERO);
                    cheapestPizza.setIsFree(true);
                }
                
                // Réinitialiser le compteur de fidélité
                clientService.resetLoyaltyCounter(updatedClient);
                
                log.info("Pizza gratuite : {} (taille: {}, quantité: 1)", 
                    cheapestPizza.getPizza().getPizzaName(),
                    cheapestPizza.getPizzaSize());
            }
        }

        Order savedOrder = repository.save(order);
        
        log.info("Commande sauvegardée avec l'ID {} et {} pizzas", 
            savedOrder.getOrderId(), savedOrder.getOrderItems().size());
        log.info("Détails des pizzas après sauvegarde:");
        for (OrderPizza pizza : savedOrder.getOrderItems()) {
            log.info("- {} (taille: {}, quantité: {}, prix: {}, gratuite: {})", 
                pizza.getPizza().getPizzaName(),
                pizza.getPizzaSize(),
                pizza.getQuantity(),
                pizza.getPizzaPrice(),
                pizza.getIsFree());
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

    @Scheduled(fixedRate = 60000) // Vérifie toutes les minutes
    @Transactional
    public void checkLateOrders() {
        log.info("=== Début de la vérification des commandes en retard ===");
        List<Order> activeOrders = findActiveOrders();
        log.info("Nombre de commandes actives trouvées : {}", activeOrders.size());
        
        for (Order order : activeOrders) {
            log.debug("Vérification de la commande #{} - Statut: {}, Date: {}, FreeReason: {}", 
                order.getOrderId(),
                order.getOrderStatus(),
                order.getOrderDate(),
                order.getFreeReason());
            
            if (isLateDelivery(order) && order.getFreeReason() != FreeReason.LATE_DELIVERY) {
                log.info("🚨 Commande #{} en retard - Délai dépassé de {} minutes", 
                    order.getOrderId(),
                    Duration.between(order.getOrderDate(), LocalDateTime.now()).toMinutes() - DELIVERY_TIME_THRESHOLD_MINUTES);
                
                // Marquer la commande comme remboursée
                order.setFreeReason(FreeReason.LATE_DELIVERY);
                repository.save(order);
                log.info("✅ Commande #{} marquée comme remboursée (LATE_DELIVERY)", order.getOrderId());
                
                // Rembourser le client
                Client client = order.getClient();
                BigDecimal totalAmount = BigDecimal.valueOf(calculateOrderTotal(order));
                BigDecimal newAmount = client.getAmount().add(totalAmount).setScale(2, BigDecimal.ROUND_HALF_UP);
                clientService.updateAmount(client.getClientId(), newAmount);
                
                log.info("💰 Client #{} remboursé de {}€ - Nouveau solde: {}€", 
                    client.getClientId(), 
                    totalAmount.doubleValue(),
                    newAmount.doubleValue());
            } else if (isLateDelivery(order)) {
                log.debug("Commande #{} en retard mais déjà remboursée (FreeReason: {})", 
                    order.getOrderId(), 
                    order.getFreeReason());
            }
        }
        log.info("=== Fin de la vérification des commandes en retard ===");
    }
} 