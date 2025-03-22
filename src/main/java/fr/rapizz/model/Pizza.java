package fr.rapizz.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pizzas")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "ingredients")
public class Pizza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pizza_id")
    private Integer pizzaId;

    @Column(name = "pizza_name", nullable = false, length = 48)
    private String pizzaName;

    @Column(name = "base_price", nullable = false, precision = 5, scale = 2)
    private BigDecimal basePrice;

    @ManyToMany
    @JoinTable(
            name = "pizzas_ingredients",
            joinColumns = @JoinColumn(name = "pizza_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private Set<Ingredient> ingredients = new HashSet<>();
}
