package org.qtee.dashboard.data.projection;

import java.time.LocalDateTime;

public interface ShipmentDayStat {
    LocalDateTime getDay();
    Long getCount();
    Long getSum();
}
