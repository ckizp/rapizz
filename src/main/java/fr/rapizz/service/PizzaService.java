package fr.rapizz.service;

import fr.rapizz.model.Pizza;
import fr.rapizz.model.PizzaSize;
import fr.rapizz.repository.PizzaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PizzaService {
    private final PizzaRepository repository;

    public List<Pizza> findAll() {
        return repository.findAllWithIngredients();
    }

    public Optional<Pizza> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public Pizza save(Pizza pizza) {
        return repository.save(pizza);
    }

    @Transactional
    public void delete(Pizza pizza) {
        repository.delete(pizza);
    }

    @Transactional
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    public BigDecimal calculatePrice(Pizza pizza, PizzaSize size) {
        BigDecimal basePrice = pizza.getBasePrice();

        return switch (size) {
            case NAINE -> basePrice.multiply(new BigDecimal("0.67"));
            case HUMAINE -> basePrice;
            case OGRESSE -> basePrice.multiply(new BigDecimal("1.33"));
            default -> throw new IllegalArgumentException("Invalid pizza size");
        };
    }
}
