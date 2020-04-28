package ua.com.tracktor.entity.enums;

public enum DeliveryService {
    NOWA_POSHTA, UKRPOSHTA;

    public static String getLogoPath(DeliveryService deliveryService) {
        if (deliveryService == null) {
            return "/images/UnknownDeliveryService.png";
        }

        switch (deliveryService) {
            case NOWA_POSHTA:
                return "/images/NovaPoshta.png";
            case UKRPOSHTA:
                return "/images/Ukrposhta.png";
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
            default:
                return "";
        }
    }
}
