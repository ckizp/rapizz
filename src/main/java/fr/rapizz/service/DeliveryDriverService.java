package fr.rapizz.service;

import fr.rapizz.model.DeliveryDriver;
import fr.rapizz.repository.DeliveryDriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryDriverService {
    private final DeliveryDriverRepository repository;
    private final OrderService orderService;

    public List<DeliveryDriver> findAll() {
        return repository.findAll();
    }

    public List<DeliveryDriver> findAvailableDrivers() {
        List<DeliveryDriver> allDrivers = findAll();
        Set<Integer> occupiedDriverIds = orderService.getOccupiedDriverIds();

        return allDrivers.stream()
                .filter(driver -> !occupiedDriverIds.contains(driver.getDriverId()))
                .collect(Collectors.toList());
    }

    public Optional<DeliveryDriver> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public DeliveryDriver save(DeliveryDriver driver) {
        return repository.save(driver);
    }

    @Transactional
    public DeliveryDriver update(DeliveryDriver driver) {
        return repository.save(driver);
    }

    @Transactional
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}