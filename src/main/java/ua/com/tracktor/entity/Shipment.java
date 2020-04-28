package ua.com.tracktor.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.com.tracktor.entity.enums.DeliveryService;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shipments")
@Data
@NoArgsConstructor
public class Shipment {
    @Id
    private UUID id;

    @ManyToOne(targetEntity = Account.class)
    @JoinColumn(name = "account_id")
    private Account account;

    private LocalDateTime date;
    private Long sum;
    private String customer;
    private String phone;
    private String address;

    @Column(name = "declaration_number")
    private String declarationNumber;

    @Column(name = "delivery_service")
    private DeliveryService deliveryService;
}
