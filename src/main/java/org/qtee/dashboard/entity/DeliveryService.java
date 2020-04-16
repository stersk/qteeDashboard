package org.qtee.dashboard.entity;

public enum DeliveryService {
    NOWA_POSHTA, UKRPOSHTA;

    public static String getLogoPath(DeliveryService deliveryService) {
        switch (deliveryService) {
            case NOWA_POSHTA:
                return "/images/deliveryService/1.png";
            case UKRPOSHTA:
                return "/images/deliveryService/2.png";
            default:
                return "";
        }
    }
}
