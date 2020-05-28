package ua.com.tracktor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.tracktor.data.ShipmentRepository;
import ua.com.tracktor.data.projection.ShipmentDayStat;
import ua.com.tracktor.entity.Account;
import ua.com.tracktor.entity.Shipment;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShipmentService {
    @Autowired
    private ShipmentRepository shipmentRepository;

    public List<Shipment> getAll() {
        return shipmentRepository.findAll();
    }

    public List<Shipment> getAllInRange(LocalDateTime from, LocalDateTime to, Account account) {
        return shipmentRepository.findShipmentsByAccountAndDateBetweenOrderByDateDesc(account, from, to);
    }

    public List<ShipmentDayStat> getDayStats(LocalDateTime from, LocalDateTime to, Account account) {
        return shipmentRepository.getStatisticsByDays(from, to, account.getId());
    }

    public void save(Shipment shipment) {
        shipmentRepository.saveAndFlush(shipment);
    }

    public Long getTotalSum(Account account){
        return shipmentRepository.getTotalSum(account);
    }

    public Long getShipmentsCount(Account account){
        return shipmentRepository.getShipmentsCount(account);
    }
}
