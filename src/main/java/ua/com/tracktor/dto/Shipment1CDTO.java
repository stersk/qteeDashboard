package ua.com.tracktor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.com.tracktor.entity.Shipment;
import ua.com.tracktor.entity.enums.DeliveryService;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipment1CDTO {
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
        Float tempSum = sum * 100;
        Long sumLong = tempSum.longValue();

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
