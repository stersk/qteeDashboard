package ua.com.tracktor.entity.enums;

public enum MetricType {
    BALANSE(true),
    SHIPMENTS_LEFT(false),
    LAST_INVOICE(false),
    SHIPMENTS_COUNT_BY_DAY(false),
    SHIPMENTS_SUM_BY_DAY(false);

    public Boolean getNotifySupport() {
        return notifySupport;
    }

    private final Boolean notifySupport;

    MetricType(boolean b) {
        this.notifySupport = b;
    }

    public static MetricType getByName(String name){
        MetricType metricType = null;

        if (name != null) {
            switch (name) {
                case "balance":
                    metricType = BALANSE;
                    break;

                case "shipmentsLeft":
                    metricType = SHIPMENTS_LEFT;
                    break;

                case "lastInvoice":
                    metricType = LAST_INVOICE;
                    break;

                case "shipmentsCountByDay":
                    metricType = SHIPMENTS_COUNT_BY_DAY;
                    break;

                case "shipmentSumByDay":
                    metricType = SHIPMENTS_SUM_BY_DAY;
                    break;

                default:
                    metricType = null;
            }
        }

        return metricType;
    }
}
