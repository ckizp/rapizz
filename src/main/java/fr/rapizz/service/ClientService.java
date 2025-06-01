package fr.rapizz.service;

import fr.rapizz.model.Client;
import fr.rapizz.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientService {
    private final ClientRepository repository;

    public List<Client> findAll() {
        return repository.findAll();
    }

    public Optional<Client> findById(Integer id) {
        return repository.findById(id);
    }

    @Transactional
    public Client save(Client client) {
        return repository.save(client);
    }

    @Transactional
    public Client update(Client client) {
        return repository.save(client);
    }

    @Transactional
    public void delete(Client client) {
        repository.delete(client);
    }

    @Transactional
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    @Transactional
    public Client incrementLoyaltyCounter(Client client, int count) {
        repository.incrementLoyaltyCounter(client.getClientId(), count);
        return repository.findById(client.getClientId()).orElseThrow();
    }

    @Transactional
    public Client resetLoyaltyCounter(Client client) {
        repository.resetLoyaltyCounter(client.getClientId());
        return repository.findById(client.getClientId()).orElseThrow();
    }

    @Transactional
    public void updateLoyaltyCounter(Integer clientId, int newCount) {
        Client client = repository.findById(clientId).orElseThrow();
        client.setLoyaltyCounter(newCount);
        repository.save(client);
    }

    @Transactional
    public void updateAmount(Integer clientId, BigDecimal newAmount) {
        repository.updateAmount(clientId, newAmount);
    }
}
