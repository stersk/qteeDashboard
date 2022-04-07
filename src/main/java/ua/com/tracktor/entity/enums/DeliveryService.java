package ua.com.tracktor.entity.enums;

public enum DeliveryService {
    NOWA_POSHTA, UKRPOSHTA, MEEST, DELIVERY;

    public static String getLogoPath(DeliveryService deliveryService) {
        if (deliveryService == null) {
            return "/images/UnknownDeliveryService.png";
        }

        switch (deliveryService) {
            case NOWA_POSHTA:
                return "/images/NovaPoshta.svg";
            case UKRPOSHTA:
                return "/images/Ukrposhta.svg";
            case MEEST:
                return "/images/Meest.svg";
            case DELIVERY:
                return "/images/Delivery.svg";
            default:
                return "/images/UnknownDeliveryService.png";
        }
    }

    public static String getName(DeliveryService deliveryService) {
        if (deliveryService == null) {
            return "";
        }

        switch (deliveryService) {
            case NOWA_POSHTA:
                return "Нова Пошта";
            case UKRPOSHTA:
                return "Укрпошта";
            case MEEST:
                return "Meest Express";
            case DELIVERY:
                return "Delivery";
            default:
                return "<Невідома служба>";
        }
    }
}
