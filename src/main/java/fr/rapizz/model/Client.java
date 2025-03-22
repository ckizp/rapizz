package fr.rapizz.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "first_name", length = 48)
    private String firstName;

    @Column(name = "last_name", length = 48)
    private String lastName;

    @Column(name = "client_address", length = 256)
    private String clientAddress;

    @Column(name = "phone_number", length = 16)
    private String phoneNumber;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal amount = new BigDecimal(0);

    @Column(nullable = false)
    private Integer loyaltyCounter;
}
