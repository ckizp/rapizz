package fr.rapizz.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entity representing a pizza item within an order.
 *
 * <p>Mapped to the {@code order_pizzas} table in the database. Acts as a join
 * table between {@code orders} and {@code pizzas}, enriched with additional
 * order-specific details such as quantity, selected size, and unit price.</p>
 *
 * <p>Primary key: {@code order_item_id} (INT NOT NULL AUTO_INCREMENT).</p>
 *
 * <ul>
 *   <li><b>order_id</b>: foreign key to {@code orders}</li>
 *   <li><b>pizza_id</b>: foreign key to {@code pizzas}</li>
 *   <li><b>quantity</b>: INT NOT NULL DEFAULT 1</li>
 *   <li><b>pizza_size</b>: ENUM('NAINE', 'HUMAINE', 'OGRESSE') NOT NULL</li>
 *   <li><b>pizza_price</b>: DECIMAL(5, 2) NOT NULL</li>
 * </ul>
 *
 * <p>Many-to-one relations to {@code Order} and {@code Pizza}, both lazily loaded.</p>
 */
@Entity
@Table(name = "order_pizzas")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "orderItemId")
@ToString(exclude = {"order", "pizza"})
public class OrderPizza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Integer orderItemId;

    /**
     * The order this item belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    /**
     * The pizza that was ordered.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pizza_id")
    private Pizza pizza;

    /**
     * Number of this pizza in the order.
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    /**
     * Size of the pizza.
     * Affects the final price.
     * @see PizzaSize
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "pizza_size", nullable = false)
    private PizzaSize pizzaSize = PizzaSize.HUMAINE;

    /**
     * The price for this specific pizza item.
     * This is stored separately from the Pizza's base price to preserve
     * the price at time of order and account for size adjustments.
     */
    @Column(name = "pizza_price", nullable = false, precision = 5, scale = 2)
    private BigDecimal pizzaPrice;
}
