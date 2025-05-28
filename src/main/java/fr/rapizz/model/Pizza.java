package fr.rapizz.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a pizza from the menu.
 *
 * <p>Mapped to the {@code pizzas} table in the database. Contains core attributes
 * like name and base price. Associated with {@link Ingredient} via a many-to-many
 * relationship, and with {@link OrderPizza} for tracking orders.</p>
 *
 * <p>Primary key: {@code pizza_id} (auto-incremented).</p>
 *
 * <ul>
 *   <li><b>pizza_name</b>: VARCHAR(48), not null</li>
 *   <li><b>base_price</b>: DECIMAL(5,2), not null — price for medium size</li>
 * </ul>
 */
@Entity
@Table(name = "pizzas")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "pizzaId")
@ToString(exclude = {"ingredients", "orderItems"})
public class Pizza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pizza_id")
    private Integer pizzaId;

    @Column(name = "pizza_name", nullable = false, length = 48)
    private String pizzaName;

    /**
     * The base price of the pizza (medium size).
     * Final price depends on size selection in the order.
     */
    @Column(name = "base_price", nullable = false, precision = 5, scale = 2)
    private BigDecimal basePrice;

    /**
     * Ingredients that compose this pizza.
     */
    @ManyToMany
    @JoinTable(
            name = "pizzas_ingredients",
            joinColumns = @JoinColumn(name = "pizza_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private Set<Ingredient> ingredients = new HashSet<>();

    /**
     * Orders that include this pizza.
     */
    @OneToMany(mappedBy = "pizza", fetch = FetchType.LAZY)
    private Set<OrderPizza> orderItems = new HashSet<>();

    /**
     * Adds an ingredient to this pizza.
     *
     * @param ingredient The ingredient to add to this pizza
     */
    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
        ingredient.getPizzas().add(this);
    }

    /**
     * Removes an ingredient from this pizza.
     *
     * @param ingredient The ingredient to remove from this pizza
     */
    public void removeIngredient(Ingredient ingredient) {
        ingredients.remove(ingredient);
        ingredient.getPizzas().remove(this);
    }
}
