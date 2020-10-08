package ua.com.tracktor.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.com.tracktor.entity.Shipment;
import ua.com.tracktor.entity.enums.DeliveryService;

import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor(access= AccessLevel.PRIVATE, force=true)
public class ShipmentBootstrapTableDTO {
    private final String date;
    private final String deliveryService;
    private final String deliveryServiceName;
    private final String phone;
    private final String declarationNumber;
    private final float sum;
    private final String customer;
    private final String address;

    public ShipmentBootstrapTableDTO(Shipment shipment) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        this.date = shipment.getDate().format(dateFormatter);

        if (shipment.getDeclarationNumber() == null) {
            // When registered shipment with base data (fallback mode for failure)
            this.deliveryService = DeliveryService.getLogoPath(shipment.getDeliveryService());
            this.deliveryServiceName = DeliveryService.getName(shipment.getDeliveryService());
            this.phone = "";
            this.sum = 0f;
            this.customer = "";
            this.address = "";
            this.declarationNumber = "";
        } else {
            // When registered shipment with whole data
            this.deliveryService = DeliveryService.getLogoPath(shipment.getDeliveryService());
            this.deliveryServiceName = DeliveryService.getName(shipment.getDeliveryService());
            this.phone = shipment.getPhone();
            this.sum = (float) shipment.getSum() / 100;
            this.customer = shipment.getCustomer();
            this.address = shipment.getAddress();
            this.declarationNumber = shipment.getDeclarationNumber();
        }
    }
}
