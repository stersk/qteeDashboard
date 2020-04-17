package org.qtee.dashboard.tao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.qtee.dashboard.entity.DeliveryService;
import org.qtee.dashboard.entity.Shipment;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentFrom1CTAO {
    private UUID id;
    private LocalDateTime date;
    private Float sum;
    private String customer;
    private String phone;
    private String address;
    private String deliveryService;
    private String declarationNumber;
    private String status;

    public Shipment toShipment() {
        sum = sum * 100;
        Long sumLong = sum.longValue();

        Shipment shipment = new Shipment();
        shipment.setAddress(address);
        shipment.setCustomer(customer);
        shipment.setDate(date);
        shipment.setDeclarationNumber(declarationNumber);
        shipment.setId(id);
        shipment.setPhone(phone);
        shipment.setSum(sumLong);

        switch (deliveryService) {
            case "Нова Пошта":
                shipment.setDeliveryService(DeliveryService.NOWA_POSHTA);
                break;
            case "Укрпошта" :
                shipment.setDeliveryService(DeliveryService.UKRPOSHTA);
                break;
        }

        return shipment;
    }
}
