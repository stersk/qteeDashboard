package org.qtee.dashboard.entity;

public enum DeliveryService {
    NOWA_POSHTA, UKRPOSHTA;

    public static String getLogoPath(DeliveryService deliveryService) {
        switch (deliveryService) {
            case NOWA_POSHTA:
                return "/images/NovaPoshta.png";
            case UKRPOSHTA:
                return "/images/Ukrposhta.png";
            default:
                return "";
        }
    }

    public static String getName(DeliveryService deliveryService) {
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
