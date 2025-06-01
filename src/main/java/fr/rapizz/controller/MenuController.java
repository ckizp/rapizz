package fr.rapizz.controller;

import fr.rapizz.model.Pizza;
import fr.rapizz.model.PizzaSize;
import fr.rapizz.service.PizzaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MenuController {
    private final PizzaService pizzaService;

    public List<Pizza> getAllPizzas() {
        return pizzaService.findAll();
    }

    public BigDecimal calculatePrice(Pizza pizza, PizzaSize size) {
        return pizzaService.calculatePrice(pizza, size);
    }
}
