package fr.rapizz.repository;

import fr.rapizz.model.Order;
import fr.rapizz.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT o FROM Order o " +
           "LEFT JOIN FETCH o.client " +
           "LEFT JOIN FETCH o.driver " +
           "LEFT JOIN FETCH o.vehicle " +
           "LEFT JOIN FETCH o.orderItems oi " +
           "LEFT JOIN FETCH oi.pizza " +
           "WHERE o.orderStatus = :status")
    List<Order> findByOrderStatusWithDetails(@Param("status") OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate >= :startDate")
    long countOrders(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT COALESCE(SUM(oi.pizzaPrice * oi.quantity), 0) " +
            "FROM OrderPizza oi JOIN oi.order o " +
            "WHERE o.orderDate >= :startDate AND o.orderStatus <> :canceledStatus")
    BigDecimal calculateTotalRevenue(@Param("startDate") LocalDateTime startDate,
                                     @Param("canceledStatus") OrderStatus canceledStatus);

    @Query("SELECT o.orderStatus, COUNT(o) " +
            "FROM Order o WHERE o.orderDate >= :startDate GROUP BY o.orderStatus")
    List<Object[]> getOrderStatusCounts(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT CONCAT(d.firstName, ' ', d.lastName) as driverName " +
            "FROM Order o JOIN o.driver d " +
            "WHERE o.orderDate >= :startDate AND o.orderStatus = 'DELIVERED' " +
            "GROUP BY d.driverId ORDER BY COUNT(o) DESC")
    List<String> findTopDrivers(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT FUNCTION('DATE_FORMAT', o.orderDate, '%Y-%m-%d') as day, " +
            "COALESCE(SUM(oi.pizzaPrice * oi.quantity), 0) as revenue " +
            "FROM OrderPizza oi JOIN oi.order o " +
            "WHERE o.orderDate >= :startDate AND o.orderStatus <> 'CANCELED' " +
            "GROUP BY FUNCTION('DATE_FORMAT', o.orderDate, '%Y-%m-%d') " +
            "ORDER BY day")
    List<Object[]> getRevenueByDay(@Param("startDate") LocalDateTime startDate);

    List<Order> findByClient_ClientId(Integer clientId);
    List<Order> findByDriver_driverId(Integer driverId);
    List<Order> findByOrderStatus(OrderStatus status);
    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
    List<Order> findByClient_ClientIdAndOrderStatus(Integer clientId, OrderStatus status);
}
