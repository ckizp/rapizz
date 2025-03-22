package fr.rapizz.service;

import fr.rapizz.dao.impl.PizzaDAO;
import fr.rapizz.model.Pizza;
import fr.rapizz.model.PizzaSize;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class PizzaService {
    private final PizzaDAO pizzaDAO;

    public PizzaService() {
        this.pizzaDAO = new PizzaDAO();
    }

    public List<Pizza> findAll() {
        return pizzaDAO.findAll();
    }

    public Optional<Pizza> findById(Integer id) {
        return pizzaDAO.findById(id);
    }

    public Pizza save(Pizza pizza) {
        return pizzaDAO.save(pizza);
    }

    public void delete(Pizza pizza) {
        pizzaDAO.delete(pizza);
    }

    public void deleteById(Integer id) {
        pizzaDAO.deleteById(id);
    }

    public BigDecimal calculatePrice(Pizza pizza, PizzaSize size) {
        BigDecimal basePrice = pizza.getBasePrice();

        switch (size) {
            case NAINE:
                return basePrice.multiply(new BigDecimal("0.67"));
            case HUMAINE:
                return basePrice;
            case OGRESSE:
                return basePrice.multiply(new BigDecimal("1.33"));
            default:
                throw new IllegalArgumentException("Invalid pizza size");
        }
    }
}
