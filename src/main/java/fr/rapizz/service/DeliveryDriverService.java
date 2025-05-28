package fr.rapizz.service;

import fr.rapizz.model.DeliveryDriver;
import fr.rapizz.repository.DeliveryDriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryDriverService {
    private final DeliveryDriverRepository repository;

    public List<DeliveryDriver> findAll() {
        return repository.findAll();
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