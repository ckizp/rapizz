package fr.rapizz.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a pizza order.
 *
 * <p>Mapped to the {@code orders} table in the database. Stores metadata
 * about the order such as creation timestamp, status, delivery details,
 * client feedback, and any reason for a free order.</p>
 *
 * <p>Primary key: {@code order_id} (INT NOT NULL AUTO_INCREMENT).</p>
 *
 * <ul>
 *   <li><b>client_id</b>: foreign key to {@code clients}</li>
 *   <li><b>driver_id</b>: foreign key to {@code delivery_drivers}</li>
 *   <li><b>vehicle_id</b>: foreign key to {@code vehicles}</li>
 *   <li><b>order_date</b>: DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP</li>
 *   <li><b>order_status</b>: ENUM('PENDING', 'IN_PROGRESS', 'DELIVERED', 'CANCELED') NOT NULL DEFAULT 'PENDING' — defined by {@link OrderStatus}</li>
 *   <li><b>client_rating</b>: TINYINT DEFAULT NULL CHECK (client_rating BETWEEN 0 AND 5)</li>
 *   <li><b>free_reason</b>: ENUM('NOT_FREE', 'LOYALTY', 'LATE_DELIVERY') NOT NULL DEFAULT 'NOT_FREE' — defined by {@link FreeReason}</li>
 * </ul>
 *
 * <p>One-to-many relation with {@link OrderPizza} for the pizzas included in the order.
 * Lazy-loaded many-to-one relations to {@link Client}, {@link DeliveryDriver}, and {@link Vehicle}.</p>
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "orderId")
@ToString(exclude = {"client", "driver", "vehicle", "orderItems"})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private DeliveryDriver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    /**
     * Timestamp when the order was created.
     * Automatically set to the current time and cannot be modified.
     */
    @Column(name = "order_date", nullable = false, updatable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    /**
     * Current status of the order in its lifecycle.
     * @see OrderStatus
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    /**
     * Optional rating provided by the client after delivery.
     * Scale is 1-5 where 5 is the highest satisfaction.
     */
    @Column(name = "client_rating")
    private Integer clientRating;

    /**
     * Tracks the reason if an order is provided for free.
     * @see FreeReason
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "free_reason", nullable = false)
    private FreeReason freeReason = FreeReason.NOT_FREE;

    /**
     * Pizza items included in this order.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderPizza> orderItems = new HashSet<>();

    /**
     * Adds a pizza item to this order.
     *
     * @param orderItem The pizza item to add to this order
     */
    public void addOrderItem(OrderPizza orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    /**
     * Removes a pizza item from this order.
     *
     * @param orderItem The pizza item to remove from this order
     */
    public void removeOrderItem(OrderPizza orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }
}
