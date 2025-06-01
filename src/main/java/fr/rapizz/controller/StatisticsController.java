package fr.rapizz.controller;

import fr.rapizz.model.OrderStatus;
import fr.rapizz.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatisticsController {
    private final StatisticsService statisticsService;

    public BigDecimal calculateTotalRevenue(LocalDate startDate) {
        return statisticsService.calculateTotalRevenue(startDate);
    }

    public int countOrders(LocalDate startDate) {
        return statisticsService.countOrders(startDate);
    }

    public String findTopDriver(LocalDate startDate) {
        return statisticsService.findTopDriver(startDate);
    }

    public Map<String, Integer> getMostPopularPizzas(LocalDate startDate, int limit) {
        return statisticsService.getMostPopularPizzas(startDate, limit);
    }

    public Map<OrderStatus, Integer> getOrderStatusCounts(LocalDate startDate) {
        return statisticsService.getOrderStatusCounts(startDate);
    }

    public Map<String, BigDecimal> getRevenueByTimePeriod(LocalDate startDate) {
        return statisticsService.getRevenueByTimePeriod(startDate);
    }
}
