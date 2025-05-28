package fr.rapizz.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a client.
 *
 * <p>Mapped to the {@code clients} table in the database. Includes basic
 * customer information such as name, address, phone number, account balance,
 * and loyalty points. Maintains a one-to-many relationship with {@code Order}.</p>
 *
 * <p>Primary key: {@code client_id} (INT NOT NULL AUTO_INCREMENT).</p>
 *
 * <ul>
 *   <li><b>first_name</b>: VARCHAR(48) NOT NULL</li>
 *   <li><b>last_name</b>: VARCHAR(48) NOT NULL</li>
 *   <li><b>client_address</b>: VARCHAR(256)</li>
 *   <li><b>phone_number</b>: VARCHAR(16), E.164 format</li>
 *   <li><b>amount</b>: DECIMAL(8,2) NOT NULL DEFAULT 0.0</li>
 *   <li><b>loyalty_counter</b>: INT NOT NULL DEFAULT 0</li>
 * </ul>
 */
@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "clientId")
@ToString(exclude = "orders")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Integer clientId;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 48, message = "Le prénom doit faire entre 2 et 48 caractères")
    @Column(name = "first_name", length = 48)
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 48, message = "Le nom doit faire entre 2 et 48 caractères")
    @Column(name = "last_name", length = 48)
    private String lastName;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(min = 5, max = 256, message = "L'adresse doit faire entre 5 et 256 caractères")
    @Column(name = "client_address", length = 256)
    private String clientAddress;

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Format de téléphone invalide")
    @Size(max = 16, message = "Le numéro de téléphone ne doit pas dépasser 16 caractères")
    @Column(name = "phone_number", length = 16)
    private String phoneNumber;

    @DecimalMin(value = "0.0", message = "Le solde doit être positif")
    @Digits(integer = 10, fraction = 2, message = "Format de solde invalide")
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Min(value = 0, message = "Les points de fidélité ne peuvent pas être négatifs")
    @Column(nullable = false)
    private Integer loyaltyCounter = 0;

    /**
     * Orders placed by this client.
     */
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private Set<Order> orders = new HashSet<>();

    /**
     * Adds an order to this client.
     *
     * @param order The order to associate with this client
     */
    public void addOrder(Order order) {
        orders.add(order);
        order.setClient(this);
    }

    /**
     * Removes an order from this client.
     *
     * @param order The order to remove from this client
     */
    public void removeOrder(Order order) {
        orders.remove(order);
        order.setClient(null);
    }
}
