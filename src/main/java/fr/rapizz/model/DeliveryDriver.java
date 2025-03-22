package fr.rapizz.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "delivery_drivers")
@Getter
@Setter
@NoArgsConstructor
public class DeliveryDriver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driver_id")
    private Integer driverId;

    @Column(name = "first_name", length = 48)
    private String firstName;

    @Column(name = "last_name", length = 48)
    private String lastName;

    @Column(name = "phone_number", length = 16)
    private String phoneNumber;
}
