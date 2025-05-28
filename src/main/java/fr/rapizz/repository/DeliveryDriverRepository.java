package fr.rapizz.repository;

import fr.rapizz.model.DeliveryDriver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryDriverRepository extends JpaRepository<DeliveryDriver, Integer> {

}
