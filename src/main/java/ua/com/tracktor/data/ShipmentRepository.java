package ua.com.tracktor.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.com.tracktor.data.projection.ShipmentDayStat;
import ua.com.tracktor.entity.Account;
import ua.com.tracktor.entity.Shipment;

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

    @Query(value =  "SELECT " +
            "SUM(shipment.sum) " +
            "FROM " +
            "   Shipment shipment " +
            "WHERE " +
            "shipment.account = :account")
    Long getTotalSum(@Param("account") Account account);

    @Query(value =  "SELECT " +
            "COUNT(shipment.id) " +
            "FROM " +
            "   Shipment shipment " +
            "WHERE " +
            "shipment.account = :account")
    Long getShipmentsCount(@Param("account") Account account);


    List<Shipment> findShipmentsByAccountAndDateBetween(Account account, LocalDateTime from, LocalDateTime to);
}
