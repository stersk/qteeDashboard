package ua.com.tracktor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.com.tracktor.entity.Shipment;
import ua.com.tracktor.entity.enums.DeliveryService;

import java.time.LocalDateTime;
import java.util.UUID;

//TODO: sum in JSON is not Float. Need refactoring of it to Long

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
            case "НоваяПочта":
                shipment.setDeliveryService(DeliveryService.NOWA_POSHTA);
                break;
            case "Укрпочта" :
                shipment.setDeliveryService(DeliveryService.UKRPOSHTA);
                break;
            case "МистЭкспресс" :
                shipment.setDeliveryService(DeliveryService.MEEST);
                break;
            case "Деливери" :
                shipment.setDeliveryService(DeliveryService.DELIVERY);
                break;
        }

        return shipment;
    }
}
