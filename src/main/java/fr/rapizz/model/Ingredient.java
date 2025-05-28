package fr.rapizz.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a pizza ingredient.
 *
 * <p>Mapped to the {@code ingredients} table in the database. Each ingredient
 * has a name and can be associated with multiple {@link Pizza} entities through
 * a many-to-many relationship.</p>
 *
 * <p>Primary key: {@code ingredient_id} (INT NOT NULL AUTO_INCREMENT).</p>
 *
 * <ul>
 *   <li><b>ingredient_name</b>: VARCHAR(24) NOT NULL</li>
 * </ul>
 */
@Entity
@Table(name = "ingredients")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "ingredientId")
@ToString(exclude = "pizzas")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id")
    private Integer ingredientId;

    /**
     * The name of this ingredient.
     */
    @Column(name = "ingredient_name", nullable = false, length = 24)
    private String ingredientName;

    /**
     * Pizzas that contain this ingredient.
     */
    @ManyToMany(mappedBy = "ingredients")
    private Set<Pizza> pizzas = new HashSet<>();

    /**
     * Adds this ingredient to a pizza.
     *
     * @param pizza The pizza to add this ingredient to
     */
    public void addPizza(Pizza pizza) {
        this.pizzas.add(pizza);
        pizza.getIngredients().add(this);
    }

    /**
     * Removes this ingredient from a pizza.
     *
     * @param pizza The pizza to remove this ingredient from
     */
    public void removePizza(Pizza pizza) {
        this.pizzas.remove(pizza);
        pizza.getIngredients().remove(this);
    }
}
