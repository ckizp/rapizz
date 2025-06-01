package fr.rapizz.model;

import lombok.Getter;

/**
 * Represents reasons why an item might be provided for free.
 * Used to track promotional discounts and compensations.
 */
@Getter
public enum FreeReason {
    NOT_FREE("Tarif normal"),

    /**
     * Item is free due to customer loyalty program.
     */
    LOYALTY("Fidélité"),

    /**
     * Item is free as compensation for a late delivery.
     */
    LATE_DELIVERY("Livraison tardive");

    private final String displayName;

    FreeReason(String displayName) {
        this.displayName = displayName;
    }
}
