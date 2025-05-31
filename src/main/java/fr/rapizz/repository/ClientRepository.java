package fr.rapizz.repository;

import fr.rapizz.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    @Modifying
    @Query("UPDATE Client c SET c.loyaltyCounter = c.loyaltyCounter + :count WHERE c.clientId = :clientId")
    void incrementLoyaltyCounter(@Param("clientId") Integer clientId, @Param("count") int count);

    @Modifying
    @Query("UPDATE Client c SET c.loyaltyCounter = 0 WHERE c.clientId = :clientId")
    void resetLoyaltyCounter(@Param("clientId") Integer clientId);

    @Modifying
    @Query("UPDATE Client c SET c.amount = :newAmount WHERE c.clientId = :clientId")
    void updateAmount(@Param("clientId") Integer clientId, @Param("newAmount") BigDecimal newAmount);
}
