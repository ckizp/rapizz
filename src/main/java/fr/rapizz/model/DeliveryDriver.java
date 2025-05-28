package fr.rapizz.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing a delivery driver.
 *
 * <p>Mapped to the {@code delivery_drivers} table in the database. Stores basic
 * identification and contact details. Maintains a one-to-many relationship with
 * {@link Order} for tracking assigned deliveries.</p>
 *
 * <p>Primary key: {@code driver_id} (INT NOT NULL AUTO_INCREMENT).</p>
 *
 * <ul>
 *   <li><b>first_name</b>: VARCHAR(48) NOT NULL</li>
 *   <li><b>last_name</b>: VARCHAR(48) NOT NULL</li>
 *   <li><b>phone_number</b>: VARCHAR(16)</li>
 * </ul>
 */
@Entity
@Table(name = "delivery_drivers")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "driverId")
@ToString(exclude = "orders")
public class DeliveryDriver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driver_id")
    private Integer driverId;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 48, message = "Le prénom doit faire entre 2 et 48 caractères")
    @Column(name = "first_name", length = 48)
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 48, message = "Le nom doit faire entre 2 et 48 caractères")
    @Column(name = "last_name", length = 48)
    private String lastName;

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Format de téléphone invalide")
    @Size(max = 16, message = "Le numéro de téléphone ne doit pas dépasser 16 caractères")
    @Column(name = "phone_number", length = 16)
    private String phoneNumber;

    /**
     * Orders assigned to this driver for delivery.
     */
    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    private Set<Order> orders = new HashSet<>();

    /**
     * Assigns an order to this driver.
     *
     * @param order The order to assign to this driver
     */
    public void addOrder(Order order) {
        orders.add(order);
        order.setDriver(this);
    }

    /**
     * Removes an order assignment from this driver.
     *
     * @param order The order to unassign from this driver
     */
    public void removeOrder(Order order) {
        orders.remove(order);
        order.setDriver(null);
    }
}
