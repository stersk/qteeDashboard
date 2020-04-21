package org.qtee.dashboard.data;

import org.qtee.dashboard.data.projection.ShipmentDayStat;
import org.qtee.dashboard.entity.Account;
import org.qtee.dashboard.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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
                    "WHERE" +
                    "    account_id = :account AND" +
                    "    (date BETWEEN :from AND :to )" +
                    "GROUP BY " +
                    "    date_trunc('day',date)" +
                    "ORDER BY day")
    List<ShipmentDayStat> getStatisticsByDays(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, @Param("account") Long accountId);

    List<Shipment> findShipmentsByAccountAndDateBetween(Account account, LocalDateTime from, LocalDateTime to);
}
