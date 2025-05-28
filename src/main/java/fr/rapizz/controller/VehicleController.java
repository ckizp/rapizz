package fr.rapizz.controller;

import fr.rapizz.model.Vehicle;
import fr.rapizz.model.VehicleType;
import fr.rapizz.service.ValidationService;
import fr.rapizz.service.VehicleService;
import fr.rapizz.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class VehicleController {
    private final VehicleService service;
    private final ValidationService validator;

    public List<Vehicle> getAllVehicles() {
        return service.findAll();
    }

    public List<Vehicle> searchVehicles(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return getAllVehicles();
        }

        String search = searchText.toLowerCase();
        return service.findAll().stream()
                .filter(vehicle ->
                        vehicle.getLicensePlate().toLowerCase().contains(search) ||
                                vehicle.getVehicleType().getDisplayName().toLowerCase().contains(search))
                .toList();
    }

    public Optional<Vehicle> getVehicleById(Integer id) {
        return service.findById(id);
    }

    public Result<Vehicle> createVehicle(VehicleType type, String licensePlate) {
        log.debug("Creating vehicle: {} {}", type, licensePlate);

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleType(type);
        vehicle.setLicensePlate(licensePlate.trim());

        // Validate vehicle
        List<String> errors = validator.validateEntity(vehicle);
        if (!errors.isEmpty()) {
            return Result.failure(errors);
        }

        try {
            Vehicle saved = service.save(vehicle);
            return Result.success(saved);
        } catch (Exception e) {
            return Result.failure("Erreur lors de la création du véhicule: " + e.getMessage());
        }
    }

    public Result<Vehicle> updateVehicle(Integer id, VehicleType type, String licensePlate) {
        Optional<Vehicle> vehicleOpt = service.findById(id);
        if (vehicleOpt.isEmpty()) {
            return Result.failure("Véhicule non trouvé avec l'ID: " + id);
        }

        Vehicle vehicle = vehicleOpt.get();
        vehicle.setVehicleType(type);
        vehicle.setLicensePlate(licensePlate.trim());

        // Validate vehicle
        List<String> errors = validator.validateEntity(vehicle);
        if (!errors.isEmpty()) {
            return Result.failure(errors);
        }

        try {
            Vehicle updated = service.update(vehicle);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.failure("Erreur lors de la mise à jour du véhicule: " + e.getMessage());
        }
    }

    public Result<Void> deleteVehicle(Integer id) {
        try {
            service.deleteById(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.failure("Erreur lors de la suppression du véhicule: " + e.getMessage());
        }
    }
}
