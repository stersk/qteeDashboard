package org.qtee.dashboard.service;

import org.qtee.dashboard.data.ShipmentRepository;
import org.qtee.dashboard.data.projection.ShipmentDayStat;
import org.qtee.dashboard.entity.Account;
import org.qtee.dashboard.entity.Shipment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return shipmentRepository.findShipmentsByAccountAndDateBetween(account, from, to);
    }

    public List<ShipmentDayStat> getDayStats(LocalDateTime from, LocalDateTime to, Account account) {
        return shipmentRepository.getStatisticsByDays(from, to, account.getId());
    }

    public void save(Shipment shipment) {
        shipmentRepository.saveAndFlush(shipment);
    }
}
