package org.qtee.dashboard.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.qtee.dashboard.entity.Shipment;
import org.qtee.dashboard.entity.enums.DeliveryService;

import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor(access= AccessLevel.PRIVATE, force=true)
public class ShipmentForBootstrapTableDTO {
    private final String date;
    private final String deliveryService;
    private final String deliveryServiceName;
    private final String phone;
    private final String declarationNumber;
    private final float sum;
    private final String customer;
    private final String address;

    public ShipmentForBootstrapTableDTO(Shipment shipment) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

        this.date                = shipment.getDate().format(dateFormatter);
        this.deliveryService     = DeliveryService.getLogoPath(shipment.getDeliveryService());
        this.deliveryServiceName = DeliveryService.getName(shipment.getDeliveryService());
        this.phone = shipment.getPhone();
        this.sum = (float) shipment.getSum() / 100;
        this.customer = shipment.getCustomer();
        this.address = shipment.getAddress();
        this.declarationNumber = shipment.getDeclarationNumber();
    }
}
