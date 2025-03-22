package fr.rapizz.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_pizzas")
@Getter
@Setter
@NoArgsConstructor
public class OrderPizza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Integer orderItemId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "pizza_id")
    private Pizza pizza;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "pizza_size", nullable = false)
    private PizzaSize pizzaSize;

    @Column(name = "pizza_price", nullable = false, precision = 5, scale = 2)
    private BigDecimal pizzaPrice;
}