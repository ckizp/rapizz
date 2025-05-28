package fr.rapizz.model;

import lombok.Getter;

/**
 * Represents the available size options for pizzas.
 * The size affects the final price of pizza items in orders.
 */
@Getter
public enum PizzaSize {
    /**
     * Small pizza size (25cm diameter).
     * Priced at 80% of the base price.
     */
    NAINE("Taille Naine"),

    /**
     * Medium pizza size (33cm diameter).
     * This is the standard size with regular base price.
     */
    HUMAINE("Taille Humaine"),

    /**
     * Large pizza size (40cm diameter).
     * Priced at 130% of the base price.
     */
    OGRESSE("Taille Ogresse");

    private final String displayName;

    PizzaSize(String displayName) {
        this.displayName = displayName;
    }
}
