package fr.rapizz.model;

import lombok.Getter;

/**
 * Represents the current status of an order in its lifecycle.
 * Tracks an order from creation through delivery or cancellation.
 */
@Getter
public enum OrderStatus {
    /**
     * Order has been created but not yet assigned to a delivery driver.
     * This is the initial status for all new orders.
     */
    PENDING("En cours de traitement"),

    /**
     * Order has been assigned to a driver and is currently being delivered.
     * Transition occurs when a driver is assigned to the order.
     */
    IN_PROGRESS("En cours de livraison"),

    /**
     * Order has been successfully delivered to the client.
     * Final status for completed orders.
     */
    DELIVERED("Commande livrée"),

    /**
     * Order has been canceled and will not be delivered.
     * May occur due to client request or restaurant issues.
     */
    CANCELED("Commande annulée");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }
}
