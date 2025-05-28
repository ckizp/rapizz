package fr.rapizz.repository;

import fr.rapizz.model.Pizza;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PizzaRepository extends JpaRepository<Pizza, Integer> {
    @Query("FROM Pizza p INNER JOIN FETCH p.ingredients")
    List<Pizza> findAllWithIngredients();

    @Query("SELECT p.pizzaName, COUNT(oi) as orderCount " +
            "FROM OrderPizza oi JOIN oi.pizza p JOIN oi.order o " +
            "WHERE o.orderDate >= :startDate " +
            "GROUP BY p.pizzaName ORDER BY orderCount DESC")
    List<Object[]> getMostPopularPizzas(@Param("startDate") LocalDateTime startDate, Pageable pageable);
}
