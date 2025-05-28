package fr.rapizz.service;

import fr.rapizz.model.Client;
import fr.rapizz.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
