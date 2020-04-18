package org.qtee.dashboard.data;

import org.qtee.dashboard.data.projection.ShipmentDayStat;
import org.qtee.dashboard.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {
    @Query(nativeQuery = true, value =
            "SELECT " +
                    "    date_trunc('day',date) as day, " +
                    "    sum(sum), " +
                    "    sum(1) as count " +
                    "FROM " +
                    "    shipments " +
                    "GROUP BY " +
                    "    date_trunc('day',date)" +
                    "ORDER BY day")
    List<ShipmentDayStat> getStatisticsByDays();
}
