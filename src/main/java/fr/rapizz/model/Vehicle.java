package fr.rapizz.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing a delivery vehicle.
 *
 * <p>Mapped to the {@code vehicles} table in the database. Each vehicle has a type
 * (e.g. car, motorcycle) and a unique license plate. Used to track which
 * vehicle is assigned to a given delivery {@link Order}.</p>
 *
 * <p>Primary key: {@code vehicle_id} (INT NOT NULL AUTO_INCREMENT).</p>
 *
 * <ul>
 *   <li><b>vehicle_type</b>: ENUM('CAR', 'MOTORCYCLE') NOT NULL — defined by {@link VehicleType}</li>
 *   <li><b>license_plate</b>: VARCHAR(12) NOT NULL UNIQUE</li>
 * </ul>
 */
@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "vehicleId")
@ToString(exclude = "orders")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Integer vehicleId;

    /**
     * Type of vehicle (car, motorcycle).
     * @see VehicleType
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "license_plate", length = 12, unique = true, nullable = false)
    private String licensePlate;

    /**
     * Orders that are assigned to this vehicle for delivery.
     */
    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    private Set<Order> orders = new HashSet<>();

    /**
     * Assigns an order to this vehicle.
     *
     * @param order The order to assign to this vehicle
     */
    public void addOrder(Order order) {
        orders.add(order);
        order.setVehicle(this);
    }

    /**
     * Removes an order assignment from this vehicle.
     *
     * @param order The order to unassign from this vehicle
     */
    public void removeOrder(Order order) {
        orders.remove(order);
        order.setVehicle(null);
    }
}
