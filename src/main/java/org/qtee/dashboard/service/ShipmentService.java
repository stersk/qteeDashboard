package org.qtee.dashboard.service;

import org.qtee.dashboard.data.ShipmentRepository;
import org.qtee.dashboard.entity.Shipment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipmentService {
    @Autowired
    private ShipmentRepository shipmentRepository;

    public List<Shipment> getAll () {
        return shipmentRepository.findAll();
    }
}
