package ua.com.tracktor.data.projection;

import java.time.LocalDateTime;

public interface ShipmentDayStat {
    LocalDateTime getDay();
    Long getCount();
    Long getSum();
}