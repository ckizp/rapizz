package fr.rapizz.service;

import fr.rapizz.model.Vehicle;
import fr.rapizz.repository.VehicleRepository;
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
public class VehicleService {
    private final VehicleRepository repository;
    private final OrderService orderService;

    public List<Vehicle> findAll() {
        return repository.findAll();
    }

    public List<Vehicle> findAvailableVehicles() {
        List<Vehicle> allVehicles = findAll();
        Set<Integer> occupiedVehicleIds = orderService.getOccupiedVehicleIds();

        return allVehicles.stream()
                .filter(vehicle -> !occupiedVehicleIds.contains(vehicle.getVehicleId()))
                .collect(Collectors.toList());
    }

    public Optional<Vehicle> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public Vehicle save(Vehicle vehicle) {
        return repository.save(vehicle);
    }

    @Transactional
    public Vehicle update(Vehicle vehicle) {
        return repository.save(vehicle);
    }

    @Transactional
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}