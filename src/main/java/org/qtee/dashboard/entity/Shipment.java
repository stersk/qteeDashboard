package org.qtee.dashboard.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shipments")
@Data
@NoArgsConstructor(access= AccessLevel.PRIVATE, force=true)
@RequiredArgsConstructor
public class Shipment {
    @Id
    private UUID id;

    @ManyToOne(targetEntity = Account.class)
    @JoinColumn(name = "account_id")
    private final Long account;

    private final LocalDateTime date;
    private final Long sum;
    private final String customer;
    private final String phone;
    private final String address;

    @Column(name = "delivery_service")
    private final DeliveryService deliveryService;
}
