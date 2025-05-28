package fr.rapizz.service;

import fr.rapizz.model.OrderStatus;
import fr.rapizz.repository.OrderRepository;
import fr.rapizz.repository.PizzaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StatisticsService {
    private final OrderRepository orderRepository;
    private final PizzaRepository pizzaRepository;

    /**
     * Calculates total revenue from orders since the given date.
     */
    public BigDecimal calculateTotalRevenue(LocalDate startDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            return orderRepository.calculateTotalRevenue(startDateTime, OrderStatus.CANCELED);
        } catch (Exception e) {
            log.error("Error calculating revenue", e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * Counts total orders placed since the given date.
     */
    public int countOrders(LocalDate startDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            return (int) orderRepository.countOrders(startDateTime);
        } catch (Exception e) {
            log.error("Error counting orders", e);
            return 0;
        }
    }

    /**
     * Finds the driver who delivered the most orders since the given date.
     */
    public String findTopDriver(LocalDate startDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            List<String> drivers = orderRepository.findTopDrivers(startDateTime);
            return drivers.isEmpty() ? "Aucun" : drivers.get(0);
        } catch (Exception e) {
            log.error("Error finding top driver", e);
            return "Aucun";
        }
    }

    /**
     * Gets the most popular pizzas since the given date.
     *
     * @param startDate The date from which to count
     * @param limit Maximum number of pizzas to return
     * @return Map of pizza names to order counts
     */
    public Map<String, Integer> getMostPopularPizzas(LocalDate startDate, int limit) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            List<Object[]> results = pizzaRepository.getMostPopularPizzas(startDateTime, PageRequest.of(0, limit));

            Map<String, Integer> pizzas = new LinkedHashMap<>();
            for (Object[] row : results) {
                pizzas.put((String) row[0], ((Number) row[1]).intValue());
            }
            return pizzas;
        } catch (Exception e) {
            log.error("Error getting popular pizzas", e);
            return new LinkedHashMap<>();
        }
    }

    /**
     * Gets counts of orders by status since the given date.
     */
    public Map<OrderStatus, Integer> getOrderStatusCounts(LocalDate startDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            List<Object[]> results = orderRepository.getOrderStatusCounts(startDateTime);

            return results.stream()
                    .collect(Collectors.toMap(
                            row -> (OrderStatus) row[0],
                            row -> ((Number) row[1]).intValue()
                    ));
        } catch (Exception e) {
            log.error("Error getting order status counts", e);
            return Map.of();
        }
    }

    /**
     * Gets revenue broken down by time periods since the given date.
     * Time periods depend on the selected date range (days, weeks, or months).
     */
    public Map<String, BigDecimal> getRevenueByTimePeriod(LocalDate startDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            List<Object[]> results = orderRepository.getRevenueByDay(startDateTime);

            Map<String, BigDecimal> revenue = new LinkedHashMap<>();
            for (Object[] row : results) {
                revenue.put((String) row[0], (BigDecimal) row[1]);
            }
            return revenue;
        } catch (Exception e) {
            log.error("Error getting revenue by time period", e);
            return new LinkedHashMap<>();
        }
    }
}