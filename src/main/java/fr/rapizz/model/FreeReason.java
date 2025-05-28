package fr.rapizz.model;

import lombok.Getter;

/**
 * Represents reasons why an order might be provided for free.
 * Used to track promotional discounts and compensations.
 */
@Getter
public enum FreeReason {
    NOT_FREE("Tarif normal"),

    /**
     * Order is free due to customer loyalty program.
     */
    LOYALTY("Fidélité"),

    /**
     * Order is free as compensation for a late delivery.
     */
    LATE_DELIVERY("Livraison tardive");

    private final String displayName;

    FreeReason(String displayName) {
        this.displayName = displayName;
    }
}
