package fr.rapizz.model;

import lombok.Getter;

/**
 * Represents the types of vehicles available for delivery.
 */
@Getter
public enum VehicleType {
    CAR("Voiture"),
    MOTORCYCLE("Moto");

    private final String displayName;

    VehicleType(String displayName) {
        this.displayName = displayName;
    }
}
