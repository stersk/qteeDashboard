package org.qtee.dashboard.entity.enums;

public enum MetricType {
    BALANSE(true),
    SHIPMENTS_LEFT(false);

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

                default:
                    metricType = null;
            }
        }

        return metricType;
    }
}
