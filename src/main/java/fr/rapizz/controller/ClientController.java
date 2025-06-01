package fr.rapizz.controller;

import fr.rapizz.model.Client;
import fr.rapizz.service.ClientService;
import fr.rapizz.service.ValidationService;
import fr.rapizz.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientController {
    private final ClientService service;
    private final ValidationService validator;

    public List<Client> getAllClients() {
        return service.findAll();
    }

    public List<Client> searchClients(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return getAllClients();
        }

        String search = searchText.toLowerCase();
        return service.findAll().stream()
                .filter(client ->
                        client.getFirstName().toLowerCase().contains(search) ||
                                client.getLastName().toLowerCase().contains(search) ||
                                client.getClientAddress().toLowerCase().contains(search) ||
                                client.getPhoneNumber().toLowerCase().contains(search))
                .toList();
    }

    public Optional<Client> getClientById(Integer clientId) {
        return service.findById(clientId);
    }

    public Result<Client> createClient(String firstName, String lastName,
                                       String address, String phone) {
        log.debug("Creating client: {} {} {} {}", firstName, lastName, address, phone);

        Client client = new Client();
        client.setFirstName(firstName.trim());
        client.setLastName(lastName.trim());
        client.setClientAddress(address.trim());
        client.setPhoneNumber(phone.trim());
        client.setAmount(BigDecimal.ZERO);
        client.setLoyaltyCounter(0);

        // Validate client
        List<String> errors = validator.validateEntity(client);
        if (!errors.isEmpty()) {
            return Result.failure(errors);
        }

        try {
            Client saved = service.save(client);
            return Result.success(saved);
        } catch (Exception e) {
            return Result.failure("Erreur lors de la création du client: " + e.getMessage());
        }
    }

    public Result<Client> updateClient(Integer id, String firstName, String lastName,
                                       String address, String phone,
                                       BigDecimal amount, Integer loyaltyPoints) {
        Optional<Client> clientOpt = service.findById(id);
        if (clientOpt.isEmpty()) {
            return Result.failure("Client non trouvé avec l'ID: " + id);
        }

        Client client = clientOpt.get();
        client.setFirstName(firstName.trim());
        client.setLastName(lastName.trim());
        client.setClientAddress(address.trim());
        client.setPhoneNumber(phone.trim());
        client.setAmount(amount);
        client.setLoyaltyCounter(loyaltyPoints);

        // Validate client
        List<String> errors = validator.validateEntity(client);
        if (!errors.isEmpty()) {
            return Result.failure(errors);
        }

        try {
            Client updated = service.update(client);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.failure("Erreur lors de la mise à jour du client: " + e.getMessage());
        }
    }

    public Result<Void> deleteClient(Integer id) {
        try {
            service.deleteById(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.failure("Erreur lors de la suppression du client: " + e.getMessage());
        }
    }
}
