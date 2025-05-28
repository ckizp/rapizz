package fr.rapizz.controller;

import fr.rapizz.model.DeliveryDriver;
import fr.rapizz.service.DeliveryDriverService;
import fr.rapizz.service.ValidationService;
import fr.rapizz.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DriverController {
    private final DeliveryDriverService service;
    private final ValidationService validator;

    public List<DeliveryDriver> getAllDrivers() {
        return service.findAll();
    }

    public List<DeliveryDriver> searchDrivers(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return getAllDrivers();
        }

        String search = searchText.toLowerCase();
        return service.findAll().stream()
                .filter(driver ->
                        driver.getFirstName().toLowerCase().contains(search) ||
                                driver.getLastName().toLowerCase().contains(search) ||
                                driver.getPhoneNumber().contains(search))
                .toList();
    }

    public Optional<DeliveryDriver> getDriverById(Integer id) {
        return service.findById(id);
    }

    public Result<DeliveryDriver> createDriver(String firstName, String lastName, String phone) {
        log.debug("Creating driver: {} {} {}", firstName, lastName, phone);

        DeliveryDriver driver = new DeliveryDriver();
        driver.setFirstName(firstName.trim());
        driver.setLastName(lastName.trim());
        driver.setPhoneNumber(phone.trim());

        // Valid driver
        List<String> errors = validator.validateEntity(driver);
        if (!errors.isEmpty()) {
            return Result.failure(errors);
        }

        try {
            DeliveryDriver saved = service.save(driver);
            return Result.success(saved);
        } catch (Exception e) {
            return Result.failure("Erreur lors de la création du livreur: " + e.getMessage());
        }
    }

    public Result<DeliveryDriver> updateDriver(Integer id, String firstName, String lastName, String phone) {
        Optional<DeliveryDriver> driverOpt = service.findById(id);
        if (driverOpt.isEmpty()) {
            return Result.failure("Livreur non trouvé avec l'ID: " + id);
        }

        DeliveryDriver driver = driverOpt.get();
        driver.setFirstName(firstName.trim());
        driver.setLastName(lastName.trim());
        driver.setPhoneNumber(phone.trim());

        // Valid driver
        List<String> errors = validator.validateEntity(driver);
        if (!errors.isEmpty()) {
            return Result.failure(errors);
        }

        try {
            DeliveryDriver updated = service.update(driver);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.failure("Erreur lors de la mise à jour du livreur: " + e.getMessage());
        }
    }

    public Result<Void> deleteDriver(Integer id) {
        try {
            service.deleteById(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.failure("Erreur lors de la suppression du livreur: " + e.getMessage());
        }
    }
}
